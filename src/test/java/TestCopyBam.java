import es.upc.dmag.dispatcher.AbstractTag;
import es.upc.dmag.dispatcher.GlobalRecord;
import es.upc.dmag.dispatcher.UnclosableFileOutputStream;
import htsjdk.samtools.*;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestCopyBam {
    @Test
    public void test() throws IOException {
        SamReaderFactory.setDefaultValidationStringency(ValidationStringency.SILENT);
        final SamReader reader = SamReaderFactory.makeDefault().open(
                Paths.get("/Volumes/TooCorsair5/data/modifications/9827_2#49_corrected_sortedByName.bam")
        );
        SAMRecordIterator readIterator = reader.iterator();
        List<SAMRecord> currentRecords = null;
        String currentQname = null;
        Map<Set<AbstractTag>, Integer> previousIds = new HashMap<>();
        Map<Set<AbstractTag>, List<GlobalRecord>> records = new HashMap<>();


        SAMFileWriter writer = null;
        SAMFileHeader samFileHeader = reader.getFileHeader().clone();
        samFileHeader.setSortOrder(SAMFileHeader.SortOrder.coordinate);

        FileOutputStream fileOutputStream = new FileOutputStream(
                "/Volumes/TooCorsair5/data/modifications/9827_2#49_corrected_sortedByName_test.bam"
        );
        UnclosableFileOutputStream outputStream = new UnclosableFileOutputStream(fileOutputStream);

        writer = (new SAMFileWriterFactory()).makeBAMWriter(
                samFileHeader,
                false,
                outputStream
        );

        int remaining = 5;
        while (readIterator.hasNext() && remaining > 0) {
            SAMRecord record = readIterator.next();
            writer.addAlignment(record);
            remaining--;
        }
        writer.close();
        outputStream.realClose();

    }
}
