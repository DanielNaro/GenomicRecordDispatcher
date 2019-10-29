import htsjdk.samtools.SAMRecord;

public class IsMappedRule implements Rule<IsMapped> {
    public IsMappedRule(){
        IsMapped.setRule(this);
    }

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public IsMapped[] getTag(GlobalRecord globalRecord) {
        SAMRecord[] bestRecord = globalRecord.getBestRecords();

        boolean hasOneTrue = false;
        boolean hasOneFalse = false;

        for(int record_i=0; record_i < bestRecord.length; record_i++) {
            if (bestRecord[record_i] != null) {
                if(!bestRecord[record_i].getReadUnmappedFlag()){
                    hasOneTrue = true;
                } else {
                    hasOneFalse = true;
                }
            } else {
                hasOneFalse = true;
            }
        }

        int numTags = 0;
        if(hasOneTrue){
            numTags++;
        }
        if(hasOneFalse){
            numTags++;
        }

        IsMapped[] result = new IsMapped[numTags];
        int result_i = 0;
        if(hasOneTrue){
            result[result_i] = new IsMapped(true);
            result_i++;
        }
        if(hasOneFalse) {
            result[result_i] = new IsMapped(false);
        }

        return result;
    }

    @Override
    public String getName() {
        return "HasMapped";
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
