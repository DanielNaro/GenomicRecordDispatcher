package es.upc.dmag.dispatcher;

import es.upc.dmag.dispatcher.parser.ObjectFactory;
import es.upc.dmag.dispatcher.parser.RulesType;
import htsjdk.samtools.*;
import org.apache.commons.io.input.BoundedInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.Security;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void encode(String inputPath, String databasePath,
                              String outputPath, String rulesParameterPath,
                              String securityParametersPath) throws IOException,
            PGPException, ExecutionException, InterruptedException, SQLException, JAXBException {
        Security.addProvider(new BouncyCastleProvider());
        SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<WrittingResult>> results = new ArrayList<>();

        JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        RulesType rulesType =
                ((JAXBElement<RulesType>) unmarshaller.unmarshal(Paths.get(rulesParameterPath).toFile())).getValue();


        List<Rule> rules = decodeRules(rulesType);


        Connection connection = DatabaseCreator.createDB(databasePath, rules);

        HashMap<String, PGPPublicKey> recipientKeys = new HashMap<>();

        InputStream publicKeysInputStream = new FileInputStream("ResearcherPublic.pub.asc");
        PGPPublicKey researcherKey = PGPPublicKeyReader.read(publicKeysInputStream);
        recipientKeys.put("Researcher", researcherKey);

        publicKeysInputStream = new FileInputStream("PhysicianPublic.asc");
        PGPPublicKey physicianKey = PGPPublicKeyReader.read(publicKeysInputStream);
        recipientKeys.put("Physician", physicianKey);

        publicKeysInputStream = new FileInputStream("PhysicianPublic.asc");
        PGPPublicKey userKey = PGPPublicKeyReader.read(publicKeysInputStream);
        recipientKeys.put("User", userKey);

        SecurityRule securityRule =
                SecurityRulesParser.parse(securityParametersPath);

        Dispatcher dispatcher = new Dispatcher(rules);

        final SamReader reader = SamReaderFactory.makeDefault().open(Paths.get(inputPath));
        System.out.println("opened for reading");
        SAMRecordIterator readIterator = reader.iterator();
        List<SAMRecord> currentRecords = null;
        String currentQname = null;

        CollectionGlobalRecords records = new CollectionGlobalRecords();

        FileOutputStream outputStream =  new FileOutputStream(outputPath);

        SAMFileHeader outputHeader = reader.getFileHeader().clone();
        outputHeader.setSortOrder(SAMFileHeader.SortOrder.unsorted);

        long recordsRead = 0;

        while (readIterator.hasNext()) {
            recordsRead++;
            if(recordsRead % 200000 == 0){
                System.out.println("read: "+recordsRead);
            }
            SAMRecord readSequence = readIterator.next();

            if (currentQname == null) {
                currentQname = readSequence.getReadName();
                currentRecords = new ArrayList<SAMRecord>();
                currentRecords.add(readSequence);
            } else {
                if (!currentQname.equals(readSequence.getReadName())) {
                    GlobalRecord record = new GlobalRecord(currentRecords);
                    if(currentQname.equals("HS25_09827:2:2103:15903:23024#49")){
                        System.out.println("found");
                    }

                    AbstractTag[] tags = dispatcher.getTags(record);
                    HashSet<AbstractTag> uniqueTags = new HashSet<>(Arrays.asList(tags));
                    uniqueTags.add(securityRule.getTag(record));

                    tags = new AbstractTag[uniqueTags.size()];
                    tags = uniqueTags.toArray(tags);

                    records.addRecord(new TagsCollection(tags), record);

                    currentQname = readSequence.getReadName();
                    currentRecords = new ArrayList<SAMRecord>();
                    currentRecords.add(readSequence);
                } else {
                    currentRecords.add(readSequence);
                }
            }

            if( records.getNumberRecordsInBuffer() > 750000){
                results.add(executor.submit(records.getMax(outputHeader)));
            }
        }

        while(records.hasMoreData()){
            results.add(executor.submit(records.getMax(outputHeader)));
        }

        executor.shutdown();
        for(Future<WrittingResult> resultFuture : results){
            ResultToCollectionAdder.add(connection, resultFuture.get(), outputStream, recipientKeys);
        }

        System.out.println("main finished");
    }

    private static int[] fromIntegerListToSortedArray(List<BigInteger> input){
        Set<BigInteger> orderedBoundaries = new TreeSet<BigInteger>(input);
        int[] boundaries = new int[orderedBoundaries.size()];
        int index = 0;
        for(BigInteger boundary : orderedBoundaries){
            boundaries[index] = boundary.intValueExact();
            index++;
        }
        return boundaries;
    }

    private static double[] fromDecimalListToSortedArray(List<BigDecimal> input){
        Set<BigDecimal> orderedBoundaries = new TreeSet<BigDecimal>(input);
        double[] boundaries = new double[orderedBoundaries.size()];
        int index = 0;
        for(BigDecimal boundary : orderedBoundaries){
            boundaries[index] = boundary.doubleValue();
            index++;
        }
        return boundaries;
    }

    private static List<Rule> decodeRules(RulesType rulesType){
        List<Rule> decodedRules = new ArrayList<>();
        if(rulesType.isMarkMapped()==Boolean.TRUE){
            decodedRules.add(new IsMappedRule());
        };
        if(rulesType.isMarkUnmapped()==Boolean.TRUE){
            decodedRules.add(new HasUnmappedRule());
        }
        if(rulesType.isMarkHasPairedEnded()==Boolean.TRUE){
            decodedRules.add(new HasPairedEndedRule());
        }
        if(rulesType.isMarkHasSingleEnded()==Boolean.TRUE){
            decodedRules.add(new HasSingleEndedRule());
        }
        if(rulesType.isMarkHasOpticalDuplicateRule()==Boolean.TRUE){
            decodedRules.add(new HasOpticalDuplicateRule());
        }
        if(rulesType.isMarkGroup()==Boolean.TRUE){
            decodedRules.add(new GroupRule());
        }

        RulesType.DeletionsLengthBins deletionsLengthBins =
                rulesType.getDeletionsLengthBins();
        if(deletionsLengthBins != null){
            decodedRules.add(new DeletionsLengthBinRule(fromIntegerListToSortedArray(deletionsLengthBins.getBoundary())));
        }

        RulesType.DeletionsLengthFractionBins deletionsLengthFractionBins = rulesType.getDeletionsLengthFractionBins();
        if(deletionsLengthFractionBins != null){
            decodedRules.add(new DeletionsLengthFractionBinRule(fromDecimalListToSortedArray(deletionsLengthFractionBins.getBoundary())));
        }

        RulesType.InsertionsLengthFractionBins insertionsLengthFractionBins =
                rulesType.getInsertionsLengthFractionBins();
        if(insertionsLengthFractionBins != null){
            decodedRules.add(new InsertionsLengthFractionBinRule(fromDecimalListToSortedArray(insertionsLengthFractionBins.getBoundary())));
        }

        RulesType.InsertionsLengthBins insertionsLengthBins =
                rulesType.getInsertionsLengthBins();
        if(insertionsLengthBins != null){
            decodedRules.add(new InsertionsLengthBinRule(fromIntegerListToSortedArray(insertionsLengthBins.getBoundary())));
        }

        RulesType.MismatchedBasesFractionBins mismatchedBasesFractionBins =
                rulesType.getMismatchedBasesFractionBins();
        if(mismatchedBasesFractionBins != null){
            decodedRules.add(new MismatchedBasesFractionRule(fromDecimalListToSortedArray(mismatchedBasesFractionBins.getBoundary())));
        }

        RulesType.MismatchedBasesBins mismatchedBasesBins =
                rulesType.getMismatchedBasesBins();
        if(mismatchedBasesBins != null){
            decodedRules.add(new MismatchedBasesBinRule(fromIntegerListToSortedArray(mismatchedBasesBins.getBoundary())));
        }

        RulesType.MismatchedLengthBins mismatchedLengthBins =
                rulesType.getMismatchedLengthBins();
        if(mismatchedLengthBins != null){
            decodedRules.add(new MismatchedLengthBinRule(fromIntegerListToSortedArray(mismatchedLengthBins.getBoundary())));
        }

        RulesType.MismatchedLengthFractionBins mismatchedLengthFractionBins =
                rulesType.getMismatchedLengthFractionBins();
        if(mismatchedLengthFractionBins != null){
            decodedRules.add(new MismatchedLengthFractionBinRule(fromDecimalListToSortedArray(mismatchedLengthFractionBins.getBoundary())));
        }

        RulesType.MaximalQualityBins maximalQualityBins =
                rulesType.getMaximalQualityBins();
        if(maximalQualityBins != null){
            decodedRules.add(new MaximalQualityBinRule(fromIntegerListToSortedArray(maximalQualityBins.getBoundary())));
        }

        RulesType.MinimalQualityBins minimalQualityBins =
                rulesType.getMinimalQualityBins();
        if(maximalQualityBins != null){
            decodedRules.add(new MinimalQualityBinRule(fromIntegerListToSortedArray(minimalQualityBins.getBoundary())));
        }

        RulesType.NumDeletionsBins numDeletionsBins =
                rulesType.getNumDeletionsBins();
        if(numDeletionsBins != null){
            decodedRules.add(new NumDeletionsBinRule(fromIntegerListToSortedArray(numDeletionsBins.getBoundary())));
        }

        RulesType.NumDeletionsFractionBins numDeletionsFractionBins =
                rulesType.getNumDeletionsFractionBins();
        if(numDeletionsFractionBins != null){
            decodedRules.add(new NumDeletionsFractionBinRule(fromDecimalListToSortedArray(numDeletionsFractionBins.getBoundary())));
        }

        RulesType.NumInsertionsBins numInsertionsBins =
                rulesType.getNumInsertionsBins();
        if(numInsertionsBins != null){
            decodedRules.add(new NumInsertionsBinRule(fromIntegerListToSortedArray(numInsertionsBins.getBoundary())));
        }

        RulesType.NumInsertionsFractionBins numInsertionsFractionBins =
                rulesType.getNumInsertionsFractionBins();
        if(numInsertionsFractionBins != null){
            decodedRules.add(new NumInsertionsFractionBinRule(fromDecimalListToSortedArray(numInsertionsFractionBins.getBoundary())));
        }

        RulesType.NumMismatchesBins numMismatchesBins =
                rulesType.getNumMismatchesBins();
        if(numMismatchesBins != null){
            decodedRules.add(new NumMismatchesBinRule(fromIntegerListToSortedArray(numMismatchesBins.getBoundary())));
        }

        RulesType.NumMismatchesFractionBins numMismatchesFractionBins =
                rulesType.getNumMismatchesFractionBins();
        if(numMismatchesFractionBins != null){
            decodedRules.add(new NumMismatchesFractionBinRule(fromDecimalListToSortedArray(numMismatchesFractionBins.getBoundary())));
        }

        RulesType.NumSplicesBins numSplicesBins = rulesType.getNumSplicesBins();
        if(numSplicesBins != null){
            decodedRules.add(new NumSplicesBinRule(fromIntegerListToSortedArray(numSplicesBins.getBoundary())));
        }

        RulesType.NumSplicesFractionBins numSplicesFractionBins =
                rulesType.getNumSplicesFractionBins();
        if(numSplicesFractionBins != null){
            decodedRules.add(new NumSplicesFractionBinRule(fromDecimalListToSortedArray(numSplicesFractionBins.getBoundary())));
        }

        RulesType.AlignmentScoreBins alignmentScoreBins =
                rulesType.getAlignmentScoreBins();
        if(alignmentScoreBins != null) {
            decodedRules.add(new AlignmentScoreBinRule(fromIntegerListToSortedArray(alignmentScoreBins.getBoundary())));
        }

        return decodedRules;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, SQLException, PGPException, JAXBException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        long timeStart = System.currentTimeMillis();
        if(args[0].equalsIgnoreCase("encode")){
            encode(args[1], args[2], args[3], args[4], args[5]);
        } else if(args[0].equalsIgnoreCase("decode")){
            decodeCollated(args[1], args[2], args[3], args[4]);
        } else {
            final SamReader reader = SamReaderFactory.makeDefault().open(Paths.get(args[1]));
            SAMFileHeader outputHeader = reader.getFileHeader().clone();
            outputHeader.setSortOrder(SAMFileHeader.SortOrder.unsorted);


            SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
            samFileWriterFactory.setCreateIndex(false);
            SAMFileWriter writer =samFileWriterFactory.makeBAMWriter(
                    outputHeader,
                    false,
                    Paths.get(args[2])
            );
            SamInputResource samInputResource =
                    SamInputResource.of(new File(args[1]));
            filterBam(samInputResource, writer, AndFilterParser.parse(Paths.get(args[3])) );
            writer.close();
        }
        long timeEnd = System.currentTimeMillis();

        System.out.println((double)(timeEnd -timeStart)/(double)1000);


    }

    private static void decodeCollated(
            String databaseName,
            String collatedBAMsPath,
            String outputPath,
            String filteringConfigurationPath)
            throws SQLException, IOException, PGPException, JAXBException {
        File collatedBAMs = new File(collatedBAMsPath);

        SAMFileWriter writer = null;

        AndFilterCollection andFilterCollection = AndFilterParser.parse(Paths.get(filteringConfigurationPath));

        String connectionName = "jdbc:sqlite:"+databaseName;
        System.out.println("Connection to "+connectionName);
        Connection connection = DriverManager.getConnection(connectionName);
        Statement statement = connection.createStatement();


        Set<String> availableTables = getAvailableTables(statement);
        Set<String> availableColumns = getAvailableColumns(statement);


        System.out.println(andFilterCollection.generateSelect(availableTables, availableColumns));
        ResultSet resultSet =
                statement.executeQuery(andFilterCollection.generateSelect(availableTables, availableColumns));
        while(resultSet.next()){
            System.out.println("Working on "+resultSet.getLong(1));
            long start = resultSet.getLong(2);
            long end = resultSet.getLong(3);
            boolean encrypted = resultSet.getBoolean(4);

            SamInputResource samInputResource;
            File extracted =  null;
            File fileToDecode = null;

            FileInputStream fileStream;
            InputStream inputStream;

            if(encrypted) {

                extracted = extractData(collatedBAMs, start, end);
                extracted.deleteOnExit();

                fileToDecode = PGPFileDecryptor.decrypt(extracted, "secretsKeychain.asc");
                if (fileToDecode == null) {
                    throw new InternalError();
                }
                fileToDecode.deleteOnExit();


                samInputResource = SamInputResource.of(fileToDecode);
            } else {
                fileStream = new FileInputStream(collatedBAMs);
                fileStream.getChannel().position(start);
                inputStream = new BoundedInputStream(fileStream,
                        end-start);
                samInputResource = SamInputResource.of(inputStream);
            }

            if(writer == null) {
                FileInputStream fileStreamForHeader = new FileInputStream(collatedBAMs);
                fileStreamForHeader.getChannel().position(start);
                BoundedInputStream inputStreamForHeader = new BoundedInputStream(fileStreamForHeader,
                        end - start);
                SamInputResource samInputResourceForHeader = SamInputResource.of(inputStreamForHeader);

                final SamReader reader = SamReaderFactory.makeDefault().open(samInputResourceForHeader);
                SAMFileHeader outputHeader = reader.getFileHeader().clone();
                outputHeader.setSortOrder(SAMFileHeader.SortOrder.unsorted);

                SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
                samFileWriterFactory.setCreateIndex(false);
                writer =samFileWriterFactory.makeBAMWriter(
                        outputHeader,
                        false,
                        Paths.get(outputPath)
                );
            }
            filterBam(samInputResource, writer, andFilterCollection);
            if(extracted != null) {
                if (extracted != fileToDecode) {
                    extracted.delete();
                }
                fileToDecode.delete();
            }
        }
        if(writer != null) {
            writer.close();
        }
    }

    private static Set<String> getAvailableTables(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT t.name FROM " +
                "sqlite_master" +
                " t WHERE" +
                " t" +
                ".type == 'table'");
        Set<String> result = new HashSet<>();

        while (resultSet.next()){
            result.add(resultSet.getString(1));
        }
        return result;
    }

    private static Set<String> getAvailableColumns(Statement statement) throws SQLException{
        ResultSet resultSet = statement.executeQuery("PRAGMA table_info('AUsMain')");
        Set<String> result = new HashSet<>();

        while (resultSet.next()){
            result.add(resultSet.getString(2));
        }
        return result;

    }

    private static void filterBam(SamInputResource samInputResource, SAMFileWriter writer, AndFilterCollection filterCollection) throws IOException {
        SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);
        final SamReader reader = SamReaderFactory.makeDefault().open(samInputResource);

        SAMRecordIterator readIterator = reader.iterator();
        List<SAMRecord> currentRecords = null;
        String currentQname = null;

        CollectionGlobalRecords records = new CollectionGlobalRecords();

        long recordsRead = 0;

        while (readIterator.hasNext()) {
            recordsRead++;
            if(recordsRead % 200000 == 0){
                System.out.println("read: "+recordsRead);
            }
            SAMRecord readSequence = readIterator.next();

            if (currentQname == null) {
                currentQname = readSequence.getReadName();
                currentRecords = new ArrayList<SAMRecord>();
                currentRecords.add(readSequence);
            } else {
                if (!currentQname.equals(readSequence.getReadName())) {
                    GlobalRecord record = new GlobalRecord(currentRecords);

                    if(currentQname.equals("HS25_09827:2:2103:15903:23024#49")){
                        System.out.println("found");
                    }


                    if(filterCollection.matches(record)){
                        record.write(writer);
                    }
                    currentRecords = new ArrayList<>();
                    currentQname = readSequence.getReadName();
                    currentRecords.add(readSequence);
                } else {
                    currentRecords.add(readSequence);
                }
            }
        }
        GlobalRecord record = new GlobalRecord(currentRecords);
        if(filterCollection.matches(record)){
            record.write(writer);
        }
        reader.close();
    }

    private static File extractData(File inputFilePath, long start, long end)  throws IOException {
        if(end < start){
            throw new IllegalArgumentException();
        }

        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(inputFilePath);
            inputStream.getChannel().position(start);
        }catch (IOException e){
            throw new IllegalArgumentException(e);
        }

        File outputFile;

        outputFile = File.createTempFile("extracted", "", Paths.get(".").toFile());
        outputFile.deleteOnExit();

        FileOutputStream output = new FileOutputStream(outputFile);

        long remainingCopy = end - start;
        byte[] buffer = new byte[4096];
        while(remainingCopy > 0){
            int toCopy = (int) Long.min(buffer.length, remainingCopy);
            inputStream.read(buffer, 0, toCopy);
            output.write(buffer, 0, toCopy);
            remainingCopy -= toCopy;
        }
        return outputFile;
    }
}
