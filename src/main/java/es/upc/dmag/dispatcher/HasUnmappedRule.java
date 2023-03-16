package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

public class HasUnmappedRule implements Rule<HasUnmapped>{

    public HasUnmappedRule(){
        HasUnmapped.setRule(this);
    }

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public HasUnmapped[] getTag(GlobalRecord globalRecord) {
        boolean hasOneTrue = false;
        boolean hasOneFalse = false;


        boolean hasUnmapped = false;
        for(SAMRecord samRecord : globalRecord.getReads()){
            if(samRecord.getReadUnmappedFlag()){
                hasOneTrue = true;
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
        HasUnmapped[] result = new HasUnmapped[numTags];
        int result_i=0;
        if(hasOneTrue) {
            result[result_i] = new HasUnmapped(true);
            result_i++;
        }
        if(hasOneFalse) {
            result[result_i] = new HasUnmapped(false);
        }


        return result;
    }

    @Override
    public String getName() {
        return "es.upc.dmag.dispatcher.HasUnmapped";
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
