import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class NumMismatchesFractionBinFilter implements Filter {
    private final double min;
    private final double max;
    private final boolean forAllReads;

    public NumMismatchesFractionBinFilter(double min, double max, boolean forAllReads) {
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
                int numMismatches = globalRecord.getNumMismatchOperations()[record_i];

                double numMismatchesFraction =
                        (double)numMismatches/(double)bestRecord[record_i].getCigar().getCigarElements().size();

                if(min <= numMismatchesFraction && numMismatchesFraction < max){
                    trueForOneRead = true;
                } else {
                    trueForAllReads = false;
                }
            }
        }
        if(forAllReads){
            return trueForOneRead;
        } else {
            return trueForAllReads;
        }
    }

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("NumMismatchesFractionBin")) {
            return "AUsMain.id == NumMismatchesFractionBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "NumMismatchesFractionBin.NumMismatchesFractionBin_min",
                    "NumMismatchesFractionBin.NumMismatchesFractionBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "NumMismatchesFractionBin";
    }
}

