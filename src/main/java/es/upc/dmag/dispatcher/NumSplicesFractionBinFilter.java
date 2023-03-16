package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class NumSplicesFractionBinFilter implements Filter {
    private final double min;
    private final double max;
    private final boolean forAllReads;

    public NumSplicesFractionBinFilter(double min, double max, boolean forAllReads) {
        this.min = min;
        this.max = max;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        NumSplicesFractionBin[] result = new NumSplicesFractionBin[bestRecord.length];
        int size = 0;
        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numSplices = globalRecord.getNumInsertionOperations()[record_i];

                double numSplicesFraction =
                        (double)numSplices/(double)bestRecord[record_i].getCigar().getCigarElements().size();

                if(min <= numSplicesFraction && numSplicesFraction < max){
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
        if(availableTables.contains("es.upc.dmag.dispatcher.NumSplicesFractionBin")) {
            return "AUsMain.id == es.upc.dmag.dispatcher.NumSplicesFractionBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "es.upc.dmag.dispatcher.NumSplicesFractionBin.NumSplicesFractionBin_min",
                    "es.upc.dmag.dispatcher.NumSplicesFractionBin.NumSplicesFractionBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "es.upc.dmag.dispatcher.NumSplicesFractionBin";
    }
}
