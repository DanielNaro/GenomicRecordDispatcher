import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class HasSingleEndedFilter implements Filter {
    private final boolean filteredValue;
    private final boolean forAllReads;

    public HasSingleEndedFilter(boolean filteredValue, boolean forAllReads) {
        this.filteredValue = filteredValue;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord record) {
        boolean hasSingleEnded = false;
        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(SAMRecord samRecord : record.getReads()){
            if(samRecord.getReadPairedFlag() == filteredValue){
                trueForOneRead = true;
            } else {
                trueForAllReads = false;
            }
        }

        if(forAllReads){
            return trueForAllReads == filteredValue;
        } else {
            return trueForOneRead == filteredValue;
        }
    }

    @Override
    public boolean returnsMultipleTags() {
        return false;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableColumns.contains("HasSingleEnded")) {
            return "HasSingleEnded = " + (filteredValue ? "1" : "0");
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "HasSingleEnded";
    }
}
