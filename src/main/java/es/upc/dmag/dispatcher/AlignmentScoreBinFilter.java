package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class AlignmentScoreBinFilter implements Filter{
    private final int min;
    private final int max;
    private final boolean forAllReads;


    public AlignmentScoreBinFilter(int min, int max, boolean forAllReads) {
        this.min = min;
        this.max = max;
        this.forAllReads = forAllReads;
    }

    public boolean matchesFilter(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(int record_i=0; record_i < bestRecord.length; record_i++){
            if(bestRecord[record_i] != null){
                int alignmentScore = bestRecord[record_i].getMappingQuality();

                if( min <= alignmentScore && alignmentScore < max){
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
        return false;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("AlignmentScore")) {
            return "AUsMain.id == AlignmentScore.au_id AND " + generateRangeIntersect(
                    Double.toString(min),
                    Double.toString(max),
                    "AlignmentScore.AlignmentScore_min",
                    "AlignmentScore.AlignmentScore_max"
            );
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "AlignmentScore";
    }
}
