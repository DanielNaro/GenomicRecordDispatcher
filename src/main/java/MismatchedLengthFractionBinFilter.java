import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class MismatchedLengthFractionBinFilter implements Filter {
    private final double min;
    private final double max;

    public MismatchedLengthFractionBinFilter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matchesFilter(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numMismatchedBases =
                        globalRecord.getTotalSizeMismatchOperations()[record_i];

                double fractionMismatchedBases = (double) numMismatchedBases / (double) bestRecord[record_i].getReadLength();

                if(min <= fractionMismatchedBases && fractionMismatchedBases < max){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("MismatchedLengthFraction")) {
            return "AUsMain.id == MismatchedLengthFraction.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "MismatchedLengthFraction.MismatchedLengthFraction_min",
                    "MismatchedLengthFraction.MismatchedLengthFraction_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "MismatchedLengthFraction";
    }
}
