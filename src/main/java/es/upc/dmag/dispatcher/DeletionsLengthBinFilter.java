package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class DeletionsLengthBinFilter implements Filter {
    private final int min;
    private final int max;
    private final boolean forAllReads;

    public DeletionsLengthBinFilter(int min, int max, boolean forAllReads) {
        this.min = min;
        this.max = max;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord record) {
        SAMRecord[] bestRecord = record.getBestRecords();

        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numDeletedBases = record.getTotalSizeDeletionOperations()[record_i];

                if(min <= numDeletedBases && numDeletedBases < max){
                    trueForOneRead = true;
                } else {
                    trueForAllReads = false;
                }
            }
        }
        if(forAllReads){
            return trueForAllReads;
        } else {
            return trueForOneRead;
        }
    }

    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("es.upc.dmag.dispatcher.DeletionsLengthBin")) {
            return "AUsMain.id == es.upc.dmag.dispatcher.DeletionsLengthBin.au_id AND " + generateRangeIntersect(
                    Integer.toString(min),
                    Integer.toString(max),
                    "es.upc.dmag.dispatcher.DeletionsLengthBin.DeletionsLengthBin_min",
                    "es.upc.dmag.dispatcher.DeletionsLengthBin.DeletionsLengthBin_max"
            );
        } else return "";
    }

    @Override
    public String getExtraTable() {
        return "es.upc.dmag.dispatcher.DeletionsLengthBin";
    }


}
