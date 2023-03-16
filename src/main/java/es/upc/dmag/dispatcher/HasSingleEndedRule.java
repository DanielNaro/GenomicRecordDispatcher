package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.Arrays;

public class HasSingleEndedRule implements Rule<HasSingleEnded> {
    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    public HasSingleEndedRule(){
        HasSingleEnded.setRule(this);
    }

    @Override
    public HasSingleEnded[] getTag(GlobalRecord globalRecord) {
        boolean hasOneTrue = false;
        boolean hasOneFalse = false;

        for(SAMRecord samRecord : globalRecord.getReads()){
            if(!samRecord.getReadPairedFlag()){
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
        HasSingleEnded[] result = new HasSingleEnded[numTags];
        int segment_i=0;
        if(hasOneTrue){
            result[segment_i] = new HasSingleEnded(true);
            segment_i++;
        }
        if(hasOneFalse){
            result[segment_i] = new HasSingleEnded(false);
        }
        return result;
    }

    @Override
    public String getName() {
        return "es.upc.dmag.dispatcher.HasSingleEnded";
    }

    @Override
    public String[] getSQLColumnNames() {
        return new String[] {"hasSingleEndedRead"};
    }

    @Override
    public EntryType[] getTypes() {
        EntryType[] result = new EntryType[getSQLColumnNames().length];
        Arrays.fill(result, getType());
        return result;
    }

    private EntryType getType() {
        return EntryType.BOOLEAN;
    }


}
