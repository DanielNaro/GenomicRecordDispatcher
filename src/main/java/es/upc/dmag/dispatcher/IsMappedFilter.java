package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class IsMappedFilter implements Filter {
    private final boolean filteredValue;
    private final boolean forAllReads;

    public IsMappedFilter(boolean filteredValue, boolean forAllReads) {
        this.filteredValue = filteredValue;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord record) {
        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(SAMRecord samRecord : record.getReads()){
            if(!samRecord.getReadUnmappedFlag() == filteredValue){
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

    public boolean returnsMultipleTags() {
        return false;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("HasMapped")) {
            return "AUsMain.id == HasMapped.au_id AND HasMapped.HasMapped = " + (filteredValue ? "1" : "0");
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "HasMapped";
    }


}
