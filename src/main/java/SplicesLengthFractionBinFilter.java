import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class SplicesLengthFractionBinFilter implements Filter{
    private final double min;
    private final double max;
    private final boolean forAllReads;

    public SplicesLengthFractionBinFilter(double min, double max, boolean forAllReads) {
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

                double fractionSplicedBases = (double) numSplicedBases / (double) bestRecord[record_i].getReadLength();

                if(min <= fractionSplicedBases && fractionSplicedBases < max){
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
        if(availableTables.contains("SplicesLengthFractionBin")) {
            return "AUsMain.id == SplicesLengthFractionBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "SplicesLengthFractionBin.SplicesLengthFractionBin_min",
                    "SplicesLengthFractionBin.SplicesLengthFractionBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "SplicesLengthFractionBin";
    }
}
