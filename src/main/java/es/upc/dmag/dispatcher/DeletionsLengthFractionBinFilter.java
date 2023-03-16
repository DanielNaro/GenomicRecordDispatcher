package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class DeletionsLengthFractionBinFilter implements Filter {
    final private double min;
    final private double max;
    final private boolean forAllReads;

    public DeletionsLengthFractionBinFilter(double min, double max,
                                            boolean forAllReads) {
        this.min = min;
        this.max = max;
        this.forAllReads = forAllReads;
    }


    @Override
    public boolean matchesFilter(GlobalRecord record) {
        SAMRecord[] bestRecord = record.getBestRecords();

        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numDeletedBases = record.getTotalSizeDeletionOperations()[record_i];

                double fractionDeletedBases = (double) numDeletedBases / (double) bestRecord[record_i].getReadLength();

                if(min <= fractionDeletedBases && fractionDeletedBases < max){
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

    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("es.upc.dmag.dispatcher.DeletionsLengthFractionBin")) {
            return "AUsMain.id == es.upc.dmag.dispatcher.DeletionsLengthFractionBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "es.upc.dmag.dispatcher.DeletionsLengthFractionBin.DeletionsLengthFractionBin_min",
                    "es.upc.dmag.dispatcher.DeletionsLengthFractionBin.DeletionsLengthFractionBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "es.upc.dmag.dispatcher.DeletionsLengthFractionBin";
    }
}
