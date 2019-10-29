import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class SplicesLengthBinFilter implements Filter {
    private final int min;
    private final int max;
    private final boolean forAllReads;

    public SplicesLengthBinFilter(int min, int max, boolean forAllReads) {
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
                int numSplicedBases = globalRecord.getTotalSizeSpliceOperations()[record_i];

                if(min <= numSplicedBases && numSplicedBases < max){
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
        if(availableTables.contains("SplicesLengthBin")) {
            return "AUsMain.id == SplicesLengthBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "SplicesLengthBin.SplicesLengthBin_min",
                    "SplicesLengthBin.SplicesLengthBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "SplicesLengthBin";
    }
}
