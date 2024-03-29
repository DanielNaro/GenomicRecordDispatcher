package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class MismatchedLengthBinFilter implements Filter {
    private final int min;
    private final int max;

    public MismatchedLengthBinFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matchesFilter(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int numInsertedBases = globalRecord.getTotalSizeInsertionOperations()[record_i];

                if(min <= numInsertedBases && numInsertedBases < max){
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
        if(availableTables.contains("es.upc.dmag.dispatcher.MismatchedLengthBin")) {
            return "AUsMain.id == es.upc.dmag.dispatcher.MismatchedLengthBin.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "es.upc.dmag.dispatcher.MismatchedLengthBin.MismatchedLengthBin_min",
                    "es.upc.dmag.dispatcher.MismatchedLengthBin.MismatchedLengthBin_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "es.upc.dmag.dispatcher.MismatchedLengthBin";
    }
}
