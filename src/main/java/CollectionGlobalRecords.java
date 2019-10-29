import htsjdk.samtools.SAMFileHeader;

import java.util.*;
import java.util.concurrent.Future;

public class CollectionGlobalRecords {
    private final Map<TagsCollection, List<GlobalRecord>> records;
    private long numberRecordsAbsolute;
    private long numberRecordsInBuffer;

    public CollectionGlobalRecords() {
        records = new HashMap<>();
        numberRecordsAbsolute = 0;
        numberRecordsInBuffer = 0;
    }

    public synchronized void addRecord(TagsCollection tags, GlobalRecord globalRecord) throws InterruptedException {
        boolean hadToWait = false;
        while(numberRecordsAbsolute > 1500000){
            if(!hadToWait) {
                System.out.println("Waiting");
            }
            hadToWait = true;
            wait();
        }
        if(hadToWait) {
            System.out.println("Resuming");
        }
        List<GlobalRecord> listRecords = records.computeIfAbsent(tags, k -> new ArrayList<>());
        listRecords.add(globalRecord);
        numberRecordsAbsolute++;
        numberRecordsInBuffer++;
    }

    public synchronized RecordsWriter getMax(SAMFileHeader outputHeader){
        int maxSize = -1;
        TagsCollection maxTags = null;

        for(Map.Entry<TagsCollection, List<GlobalRecord>> entry : records.entrySet()){
            if(entry.getValue().size() > maxSize){
                maxSize = entry.getValue().size();
                maxTags = entry.getKey();
            }
        }

        System.out.println("submitting of size "+maxSize);

        RecordsWriter recordsWriter = new RecordsWriter(
                records.get(maxTags),
                outputHeader,
                maxTags,
                this
        );

        records.remove(maxTags);
        numberRecordsInBuffer -= maxSize;

        return recordsWriter;
    }

    public synchronized void registerFinished(long numberFinished){
        System.out.println("number in absloute: "+ numberRecordsAbsolute);
        numberRecordsAbsolute -= numberFinished;
        System.out.println("after writing number in absloute: "+ numberRecordsAbsolute);
        notifyAll();
    }

    public synchronized boolean hasMoreData(){
        return records.size() > 0;
    }

    public synchronized long getNumberRecordsInBuffer() {
        return numberRecordsInBuffer;
    }
}