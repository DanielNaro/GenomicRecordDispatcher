import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class NumSplicesBinFilter implements Filter {
    private final int min;
    private final int max;
    private final boolean forAllReads;

    public NumSplicesBinFilter(int min, int max, boolean forAllReads) {
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
                int numSplices = globalRecord.getNumSpliceOperations()[record_i];

                if(min <= numSplices && numSplices < max){
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
        if(availableTables.contains("NumSplicesBin")) {
            return "AUsMain.id == NumSplicesBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "NumSplicesBin.NumSplicesBin_min",
                    "NumSplicesBin.NumSplicesBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "NumSplicesBin";
    }
}
