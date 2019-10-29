import dispatcher.dmag.upc.es.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SecurityRulesParser {
    public static SecurityRule parse(String securityRulesPath) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        SecurityRulesType securityRules =
                ((JAXBElement<SecurityRulesType>) unmarshaller.unmarshal(Paths.get(securityRulesPath).toFile())).getValue();


        SecurityRule securityRule = new SecurityRule();
        for(SecurityRuleType securityRuleType : securityRules.getSecurityRule()){

            SecurityRegion securityRegion;
            if(securityRuleType.getAnyRegion() != null){
                securityRegion = new SecurityAnyRegion();
            } else {
                RegionType regionType = securityRuleType.getRegion();
                securityRegion = new SecurityRegion(
                        regionType.getReferenceId(),
                        regionType.getStart().intValueExact(),
                        regionType.getEnd().intValueExact()
                );
            }
            RecipientsList recipients;
            if(securityRuleType.getAnyBody() != null){
                recipients = RecipientsList.forEveryone();
            } else {
                TreeSet<String> recipientsName = new TreeSet<>(securityRuleType.getRecipients().getRecipient());
                recipients = RecipientsList.forList(recipientsName);
            }

            securityRule.addSubRule(
                    securityRegion,
                    recipients
            );
        }
        return securityRule;
    }
}
