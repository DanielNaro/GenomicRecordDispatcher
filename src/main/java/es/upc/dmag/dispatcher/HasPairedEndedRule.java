package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

public class HasPairedEndedRule implements Rule<HasPairedEnded>{
    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    public HasPairedEndedRule(){
        HasPairedEnded.setRule(this);
    }

    @Override
    public HasPairedEnded[] getTag(GlobalRecord globalRecord) {
        boolean hasOneTrue = false;
        boolean hasOneFalse = false;

        boolean hasPairedEnded = false;
        for(SAMRecord samRecord : globalRecord.getReads()){
            if(samRecord.getReadPairedFlag()){
                hasOneTrue = true;
            } else {
                hasOneFalse = true;
            }
        }


        int numTags = 0;
        if(hasOneTrue) {
            numTags++;
        }
        if(hasOneFalse){
            numTags++;
        }
        HasPairedEnded[] result = new HasPairedEnded[numTags];

        int segment_i = 0;
        if(hasOneTrue){
            result[segment_i] = new HasPairedEnded(true);
            segment_i++;
        }
        if(hasOneFalse){
            result[segment_i] = new HasPairedEnded(false);
        }

        return result;
    }

    @Override
    public String getName() {
        return "es.upc.dmag.dispatcher.HasPairedEnded";
    }

    @Override
    public EntryType[] getTypes() {
        return new EntryType[] {EntryType.BOOLEAN};
    }


    @Override
    public String[] getSQLColumnNames() {
        return new String[] {getName()};
    }
}
