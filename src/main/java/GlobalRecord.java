import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;

import java.util.Arrays;
import java.util.List;

public class GlobalRecord {
    private static long previous_instances = 0;

    private final long id;
    private final List<SAMRecord> recordList;
    private final SAMRecord[] bestRecords;
    private final int[] numMismatchOperations;
    private final int[] totalSizeMappedOperations;
    private final int[] totalSizeMismatchOperations;
    private final int[] numInsertionOperations;
    private final int[] totalSizeInsertionOperations;
    private final int[] numDeletionOperations;
    private final int[] totalSizeDeletionOperations;
    private final int[] numSpliceOperations;
    private final int[] totalSizeSpliceOperations;
    private final byte minimalQuality;
    private final byte maximalQuality;



    public GlobalRecord(List<SAMRecord> recordList) {
        id = previous_instances;
        previous_instances++;

        this.recordList = recordList;

        byte tmp_minimalQuality = Byte.MAX_VALUE;
        byte tmp_maximalQuality = Byte.MIN_VALUE;

        bestRecords = new SAMRecord[2];
        int[] besAlignmentScore = new int[bestRecords.length];
        Arrays.fill(besAlignmentScore, Integer.MIN_VALUE);


        for(SAMRecord samRecord : recordList){
            if(!samRecord.getReadUnmappedFlag()){
                int index;
                if(samRecord.getFirstOfPairFlag()){
                    index = 0;
                }else{
                    index = 1;
                }

                if(!samRecord.getReadUnmappedFlag() && samRecord.getMappingQuality() > besAlignmentScore[index]){
                    besAlignmentScore[index] = samRecord.getMappingQuality();
                    bestRecords[index] = samRecord;
                }
            }
        }

        for(SAMRecord samRecord : bestRecords){
            if(samRecord != null) {
                for (byte quality : samRecord.getBaseQualities()) {
                    if (quality < tmp_minimalQuality) {
                        tmp_minimalQuality = quality;
                    }
                    if (quality > tmp_maximalQuality) {
                        tmp_maximalQuality = quality;
                    }
                }
            }
        }

        minimalQuality = tmp_minimalQuality;
        maximalQuality = tmp_maximalQuality;

        numMismatchOperations = new int[bestRecords.length];
        totalSizeMappedOperations = new int[bestRecords.length];
        totalSizeMismatchOperations = new int[bestRecords.length];
        numInsertionOperations = new int[bestRecords.length];
        totalSizeInsertionOperations = new int[bestRecords.length];
        numDeletionOperations = new int[bestRecords.length];
        totalSizeDeletionOperations = new int[bestRecords.length];
        numSpliceOperations = new int[bestRecords.length];
        totalSizeSpliceOperations = new int[bestRecords.length];

        for(int bestRecord_i = 0; bestRecord_i < bestRecords.length; bestRecord_i++){
            SAMRecord bestRecord = bestRecords[bestRecord_i];

            if(bestRecord == null || bestRecord.getCigar() == null){
                continue;
            }

            for(CigarElement cigarElement : bestRecord.getCigar().getCigarElements()){
                switch (cigarElement.getOperator()){
                    case M:
                        throw new IllegalArgumentException();
                    case X:
                        totalSizeMappedOperations[bestRecord_i] += cigarElement.getLength();
                        totalSizeMismatchOperations[bestRecord_i] += cigarElement.getLength();
                        numMismatchOperations[bestRecord_i] += 1;
                        break;
                    case I:
                        numInsertionOperations[bestRecord_i] += 1;
                        totalSizeInsertionOperations[bestRecord_i] += cigarElement.getLength();
                        break;
                    case D:
                        numDeletionOperations[bestRecord_i] += 1;
                        totalSizeDeletionOperations[bestRecord_i] += cigarElement.getLength();
                        break;
                    case N:
                        numSpliceOperations[bestRecord_i] += 1;
                        totalSizeSpliceOperations[bestRecord_i] += cigarElement.getLength();
                        break;
                    case EQ:
                        totalSizeMappedOperations[bestRecord_i] += cigarElement.getLength();
                        break;
                    default:
                        break;
                }
            }
        }
    }


    public Iterable<? extends SAMRecord> getReads() {
        return recordList;
    }

    public void write(SAMFileWriter writer) {
        for(SAMRecord record : recordList){
            writer.addAlignment(record);
        }
    }

    public SAMRecord[] getBestRecords() {
        return bestRecords;
    }

    public int[] getNumMismatchOperations() {
        return numMismatchOperations;
    }

    public int[] getTotalSizeMappedOperations() {
        return totalSizeMappedOperations;
    }

    public int[] getNumInsertionOperations() {
        return numInsertionOperations;
    }

    public int[] getTotalSizeInsertionOperations() {
        return totalSizeInsertionOperations;
    }

    public int[] getNumDeletionOperations() {
        return numDeletionOperations;
    }

    public int[] getTotalSizeDeletionOperations() {
        return totalSizeDeletionOperations;
    }

    public int[] getNumSpliceOperations() {
        return numSpliceOperations;
    }

    public int[] getTotalSizeSpliceOperations() {
        return totalSizeSpliceOperations;
    }

    public byte getMinimalQuality() {
        return minimalQuality;
    }

    public byte getMaximalQuality() {
        return maximalQuality;
    }

    public int[] getTotalSizeMismatchOperations() {
        return totalSizeMismatchOperations;
    }
}
