import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class NumDeletionsBinFilter implements Filter {
    private final int min;
    private final int max;
    private final boolean forAllReads;

    public NumDeletionsBinFilter(int min, int max, boolean forAllReads) {
        this.min = min;
        this.max = max;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numDeletions = globalRecord.getNumDeletionOperations()[record_i];

                if(min <= numDeletions && numDeletions < max){
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

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("NumDeletionsBin")) {
            return "AUsMain.id == NumDeletionsBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "NumDeletionsBin.NumDeletionsBin_min",
                    "NumDeletionsBin.NumDeletionsBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "NumDeletionsBin";
    }
}
