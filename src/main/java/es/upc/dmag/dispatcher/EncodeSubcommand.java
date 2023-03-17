package es.upc.dmag.dispatcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import es.upc.dmag.dispatcher.parser.ObjectFactory;
import es.upc.dmag.dispatcher.parser.RulesType;
import htsjdk.samtools.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static es.upc.dmag.dispatcher.UtilsRules.decodeRules;

@Command(name = "encode")
public class EncodeSubcommand implements Callable<Integer> {
    @CommandLine.Parameters(arity = "1", paramLabel = "INPUTFILE")
    private Path inputPath;

    @CommandLine.Parameters(arity = "1", paramLabel = "DATABASEPATH")
    private Path databasePath;

    @CommandLine.Parameters(arity = "1", paramLabel = "OUTPUTPATH")
    private Path outputPath;

    @CommandLine.Parameters(arity = "1", paramLabel = "RULESPATH")
    private Path rulesParameterPath;

    @CommandLine.Parameters(arity = "1", paramLabel = "SECURITYPARAMETERSPATH")
    private Path securityParametersPath;

    public static void encode(Path inputPath, Path databasePath,
                              Path outputPath, Path rulesParameterPath,
                              Path securityParametersPath) throws IOException,
            PGPException, ExecutionException, InterruptedException, SQLException, JAXBException {
        Security.addProvider(new BouncyCastleProvider());
        SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);

            ExecutorService executor = Executors.newFixedThreadPool(3);

            List<Future<WrittingResult>> results = new ArrayList<>();

            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<RulesType> jaxbElement = (JAXBElement<RulesType>) unmarshaller.unmarshal(rulesParameterPath.toFile());
            if (jaxbElement == null){
                throw new IllegalArgumentException();
            }
            RulesType rulesType = jaxbElement.getValue();


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

            final SamReader reader = SamReaderFactory.makeDefault().open(inputPath);
            System.out.println("opened for reading");
            SAMRecordIterator readIterator = reader.iterator();
            List<SAMRecord> currentRecords = null;
            String currentQname = null;

            CollectionGlobalRecords records = new CollectionGlobalRecords();

            FileOutputStream outputStream = new FileOutputStream(outputPath.toFile());

            SAMFileHeader outputHeader = reader.getFileHeader().clone();
            outputHeader.setSortOrder(SAMFileHeader.SortOrder.unsorted);

            long recordsRead = 0;

            while (readIterator.hasNext()) {
                recordsRead++;
                if (recordsRead % 200000 == 0) {
                    System.out.println("read: " + recordsRead);
                }
                SAMRecord readSequence = readIterator.next();

                if (currentQname == null) {
                    currentQname = readSequence.getReadName();
                    currentRecords = new ArrayList<SAMRecord>();
                    currentRecords.add(readSequence);
                } else {
                    if (!currentQname.equals(readSequence.getReadName())) {
                        GlobalRecord record = new GlobalRecord(currentRecords);
                        if (currentQname.equals("HS25_09827:2:2103:15903:23024#49")) {
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

                if (records.getNumberRecordsInBuffer() > 750000) {
                    results.add(executor.submit(records.getMax(outputHeader)));
                }
            }

            while (records.hasMoreData()) {
                results.add(executor.submit(records.getMax(outputHeader)));
            }

            executor.shutdown();
            for (Future<WrittingResult> resultFuture : results) {
                ResultToCollectionAdder.add(connection, resultFuture.get(), outputStream, recipientKeys);
            }


        System.out.println("main finished");
    }

    @Override
    public Integer call() {
        try{
            encode( inputPath,  databasePath,
                     outputPath,  rulesParameterPath,
                     securityParametersPath);
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

}