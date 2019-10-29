import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class NumDeletionsFractionBinFilter implements Filter {
    private final double min;
    private final double max;
    private final boolean forAllReads;

    public NumDeletionsFractionBinFilter(double min, double max, boolean forAllReads) {
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

                double numDeletionFraction =
                        (double)numDeletions/(double)bestRecord[record_i].getCigar().getCigarElements().size();

                if(min <= numDeletionFraction && numDeletionFraction < max){
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
        if(availableTables.contains("NumDeletionsFractionBin")) {
            return "AUsMain.id == NumDeletionsFractionBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "NumDeletionsFractionBin.NumDeletionsFractionBin_min",
                    "NumDeletionsFractionBin.NumDeletionsFractionBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "NumDeletionsFractionBin";
    }
}
