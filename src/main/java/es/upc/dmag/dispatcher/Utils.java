package es.upc.dmag.dispatcher;

import htsjdk.samtools.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {
    static int[] fromIntegerListToSortedArray(List<BigInteger> input){
        return input.stream().sorted().mapToInt(BigInteger::intValueExact).toArray();
    }

    static double[] fromDecimalListToSortedArray(List<BigDecimal> input){
        return input.stream().sorted().mapToDouble(BigDecimal::doubleValue).toArray();
    }

    static void filterBam(SamInputResource samInputResource, SAMFileWriter writer, AndFilterCollection filterCollection) throws IOException {
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

    static File extractData(File inputFilePath, long start, long end)  throws IOException {
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
