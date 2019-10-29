import htsjdk.samtools.SAMRecord;

public class HasOpticalDuplicateRule implements Rule<HasOpticalDuplicate> {
    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    public HasOpticalDuplicateRule(){
        HasOpticalDuplicate.setRule(this);
    }

    @Override
    public HasOpticalDuplicate[] getTag(GlobalRecord globalRecord) {
        boolean hasOneTrue = false;
        boolean hasOneFalse = false;

        for(SAMRecord samRecord : globalRecord.getReads()){
            if(samRecord.getDuplicateReadFlag()){
                hasOneTrue = true;
            } else {
                hasOneFalse = true;
            }
        }
        int numSegments = 0;
        if(hasOneTrue){
            numSegments++;
        }
        if(hasOneFalse){
            numSegments++;
        }

        HasOpticalDuplicate[] result = new HasOpticalDuplicate[numSegments];
        int segment_i=0;
        if(hasOneTrue){
            result[segment_i] = new HasOpticalDuplicate(true);
            segment_i++;
        }
        if(hasOneFalse){
            result[segment_i] = new HasOpticalDuplicate(false);
        }
        return result;
    }

    @Override
    public String getName() {
        return "HasOpticalDuplicate";
    }

    @Override
    public EntryType[] getTypes() {
        return new EntryType[]{EntryType.BOOLEAN};
    }


    @Override
    public String[] getSQLColumnNames() {
        return new String[] {getName()};
    }
}
