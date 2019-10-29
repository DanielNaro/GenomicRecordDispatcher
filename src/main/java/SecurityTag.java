import java.util.*;

public class SecurityTag extends AbstractTag{
    private final String[] recipients;

    @Override
    Rule getRule() {
        return null;
    }


    public SecurityTag(String[] recipients) {
        Arrays.sort(recipients);
        this.recipients = recipients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityTag)) return false;
        SecurityTag that = (SecurityTag) o;
        if(recipients.length != that.recipients.length){
            return false;
        }
        for(int i=0; i<recipients.length; i++){
            if(!recipients[i].equalsIgnoreCase(that.recipients[i])){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipients);
    }

    @Override
    public String[] getValuesForSQL() {
        return null;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            SecurityTag castedO = (SecurityTag) o;
            if(recipients.length != castedO.recipients.length){
                return Integer.compare(recipients.length,
                        castedO.recipients.length);
            }
            int sizeToCompare = recipients.length;


            for(int i=0; i<sizeToCompare; i++){
                String thisRecipient = recipients[i];
                String thatRecipient = castedO.recipients[i];

                if(!thisRecipient.equals(thatRecipient)){
                    return thisRecipient.compareToIgnoreCase(thatRecipient);
                }
            }

            return 0;
        }
    }

    public boolean getRecipientsContains(String everybody) {
        for(String recipient : recipients){
            if(recipient.equalsIgnoreCase(everybody)){
                return true;
            }
        }
        return false;
    }
}
