import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class NumInsertionsBinFilter implements Filter {
    private final int min;
    private final int max;
    private final boolean forAllReads;

    public NumInsertionsBinFilter(int min, int max, boolean forAllReads) {
        this.min = min;
        this.max = max;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        NumInsertionsBin[] result = new NumInsertionsBin[bestRecord.length];
        int size = 0;
        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numInsertions = globalRecord.getNumInsertionOperations()[record_i];

                if(min <= numInsertions && numInsertions < max){
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
        if(availableTables.contains("NumInsertionsBin")) {
            return "AUsMain.id == NumInsertionsBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "NumInsertionsBin.NumInsertionsBin_min",
                    "NumInsertionsBin.NumInsertionsBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "NumInsertionsBin";
    }
}
