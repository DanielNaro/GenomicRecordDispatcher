import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class HasUnmappedFilter implements Filter {
    private final boolean filteredValue;
    private final boolean forAllReads;

    public HasUnmappedFilter(boolean filteredValue, boolean forAllReads) {
        this.filteredValue = filteredValue;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord record) {
        boolean trueForAllReads = true;
        boolean trueForOneRead = false;
        for(SAMRecord samRecord : record.getReads()){
            if(samRecord.getReadUnmappedFlag()){
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
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("HasUnmapped")) {
            return "AUsMain.id == HasUnmapped.au_id AND HasUnmapped" +
                    ".HasUnmapped = " + (filteredValue ? "1" : "0");
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "HasUnmapped";
    }
}
