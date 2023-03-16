package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class HasPairedEndedFilter implements Filter {
    private final boolean filteredValue;
    private final boolean forAllReads;

    public HasPairedEndedFilter(boolean filteredValue, boolean forAllReads) {
        this.filteredValue = filteredValue;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord record) {
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
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("es.upc.dmag.dispatcher.HasPairedEnded")) {
            return "AUsMain.id == es.upc.dmag.dispatcher.HasPairedEnded.au_id AND es.upc.dmag.dispatcher.HasPairedEnded" +
                    ".es.upc.dmag.dispatcher.HasPairedEnded = " + (filteredValue ? "1" : "0");
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "es.upc.dmag.dispatcher.HasPairedEnded";
    }
}
