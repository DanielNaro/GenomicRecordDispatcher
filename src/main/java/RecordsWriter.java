import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class RecordsWriter implements Callable<WrittingResult> {
    private final List<GlobalRecord> records;
    private final SAMFileHeader samFileHeader;
    private final TagsCollection maxTags;
    private final CollectionGlobalRecords collectionGlobalRecords;

    private File file;

    public RecordsWriter(List<GlobalRecord> records, SAMFileHeader samFileHeader, TagsCollection maxTags, CollectionGlobalRecords collectionGlobalRecords) {
        this.records = records;
        this.samFileHeader = samFileHeader;
        this.maxTags = maxTags;
        this.collectionGlobalRecords = collectionGlobalRecords;
    }

    @Override
    public WrittingResult call() throws Exception {
        try {
            file = null;

            SAMFileWriter writer = null;

            file = File.createTempFile("dispatcher", ".bam", Paths.get(".").toFile());
            file.deleteOnExit();

            SAMFileWriterFactory samFileWriterFactory = new SAMFileWriterFactory();
            samFileWriterFactory.setCreateIndex(false);
            writer = samFileWriterFactory.makeBAMWriter(
                    samFileHeader,
                    false,
                    file.toPath()
            );



            for(int i=0; i < records.size(); i++){
                GlobalRecord globalRecord = records.get(i);
                globalRecord.write(writer);
            }

            writer.close();
            collectionGlobalRecords.registerFinished(records.size());
            records.clear();

            return new WrittingResult(file, maxTags);
        } catch (IOException exception){
            exception.printStackTrace();
            return null;
        }
    }
}
