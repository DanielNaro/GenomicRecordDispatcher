import htsjdk.samtools.SAMRecord;

import java.util.*;

public class Dispatcher {
    private List<Rule> rules;

    public Dispatcher(List<Rule> rules){
        this.rules = new ArrayList<>(rules);
    }

    AbstractTag[] getTags(GlobalRecord record){
        AbstractTag[] finalResult = new AbstractTag[32];
        int sizeFinalResult = 0;
        for(Rule rule : rules){
            AbstractTag[] abstractTags = rule.getTag(record);
            while(sizeFinalResult + abstractTags.length > finalResult.length){
                finalResult = Arrays.copyOf(finalResult, finalResult.length*2);
            }
            System.arraycopy(abstractTags, 0, finalResult, sizeFinalResult,
                    abstractTags.length);
            sizeFinalResult += abstractTags.length;
        }
        return Arrays.copyOf(finalResult, sizeFinalResult);
    }

}
