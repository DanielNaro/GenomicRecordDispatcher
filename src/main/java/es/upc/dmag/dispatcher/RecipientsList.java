package es.upc.dmag.dispatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RecipientsList {
    private final boolean forEverybody;
    private final Set<String> listOfRecipients;

    private RecipientsList(boolean forEverybody, Set<String> listOfRecipients) {
        this.forEverybody = forEverybody;
        this.listOfRecipients = listOfRecipients;
    }

    public static RecipientsList forEveryone(){
        return new RecipientsList(true, null);
    }

    public static RecipientsList forList(Set<String> list){
        return new RecipientsList(false, list);
    }

    public static RecipientsList combine(Collection<RecipientsList> recipientsLists){
        boolean forEveryBody = true;
        Set<String> recipients = new HashSet<>();
        for(RecipientsList recipientsList : recipientsLists){
            if(!recipientsList.forEverybody){
                if(forEveryBody){
                    recipients = recipientsList.listOfRecipients;
                    forEveryBody = false;
                } else {
                    recipients.retainAll(recipientsList.listOfRecipients);
                }
            }
        }
        return new RecipientsList(forEveryBody, recipients);
    }

    public boolean isForEverybody() {
        return forEverybody;
    }

    public String[] getRecipients() {
        String[] recipients = new String[listOfRecipients.size()];
        int pos = 0;
        for(String recipient : listOfRecipients){
            recipients[pos] = recipient;
            pos++;
        }
        return recipients;
    }
}
