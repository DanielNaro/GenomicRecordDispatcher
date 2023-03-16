package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class InsertionsLengthFractionBinFilter implements Filter {
    private final double min;
    private final double max;
    private final boolean forAllReads;

    public InsertionsLengthFractionBinFilter(double min, double max, boolean forAllReads) {
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
                int numInsertedBases = globalRecord.getTotalSizeInsertionOperations()[record_i];

                double fractionInsertedBases = (double) numInsertedBases / (double) bestRecord[record_i].getReadLength();

                if(min <= fractionInsertedBases && fractionInsertedBases < max){
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
        if(availableTables.contains("InsertionsLengthFraction")) {
            return "AUsMain.id == InsertionsLengthFraction.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "InsertionsLengthFraction.InsertionsLengthFraction_min",
                    "InsertionsLengthFraction.InsertionsLengthFraction_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "InsertionsLengthFraction";
    }
}
