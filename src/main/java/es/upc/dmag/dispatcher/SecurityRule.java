package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

import java.util.*;

public class SecurityRule{
    private final Map<SecurityRegion, RecipientsList> subrules;

    public SecurityRule() {
        this.subrules = new HashMap<>();
    }

    public void addSubRule(SecurityRegion securityRegion, RecipientsList recipients){
        subrules.put(securityRegion, recipients);
    }

    AbstractTag getTag(GlobalRecord record) {
        List<RecipientsList> recipientsLists = new ArrayList<>();
        for (SAMRecord read : record.getReads()){
            for(Map.Entry<SecurityRegion, RecipientsList> subrule : subrules.entrySet()){
                if(subrule.getKey().intersects(read)) {
                    recipientsLists.add(subrule.getValue());
                }
            }
        }

        if (recipientsLists.size() == 0){
            return new SecurityTag(new String[]{});
        }else{
            RecipientsList recipientsList = RecipientsList.combine(recipientsLists);
            if(recipientsList.isForEverybody()){
                String[] recipients = {"Everybody"};
                return new SecurityTag(recipients);
            }else{
                return new SecurityTag(recipientsList.getRecipients());
            }
        }
    }
}
