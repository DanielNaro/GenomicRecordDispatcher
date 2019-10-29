import htsjdk.samtools.SAMRecord;

public class SecurityRegion {
    private final boolean matchUnmapped;
    private final int referenceId;
    private final long startPos;
    private final long endPos;

    public static SecurityRegion matchUnmapped(){
        return new SecurityRegion(true, 0, 0, 0);
    }

    private SecurityRegion(boolean matchUnmapped, int referenceId, long startPos, long endPos) {
        this.matchUnmapped = matchUnmapped;
        this.referenceId = referenceId;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public SecurityRegion(int referenceId, long startPos, long endPos) {
        this(false, referenceId, startPos, endPos);
    }

    public boolean intersects(SAMRecord samRecord){
        if(samRecord.getReadUnmappedFlag()){
            return matchUnmapped;
        }
        if( referenceId == samRecord.getReferenceIndex()){
            long maxStart = Long.max(startPos, samRecord.getAlignmentStart());
            long minEnd = Long.min(endPos, samRecord.getAlignmentEnd());

            return maxStart < minEnd;
        }
        return false;
    }
}
