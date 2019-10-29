import htsjdk.samtools.SAMRecord;

import java.util.Arrays;
import java.util.HashSet;

public class GroupRule implements Rule<HasGroupData> {

    public GroupRule(){
        HasGroupData.setRule(this);
    }

    @Override
    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public HasGroupData[] getTag(GlobalRecord globalRecord) {
        HashSet<String> groups = new HashSet<>();

        for(SAMRecord samRecord : globalRecord.getReads()){
            groups.add(samRecord.getReadGroup().getId());
        }

        HasGroupData[] result = new HasGroupData[groups.size()];
        int i=0;
        for(String group: groups){
            result[i] = new HasGroupData(group);
        }

        return result;
    }

    @Override
    public String getName() {
        return "GroupData";
    }

    @Override
    public EntryType[] getTypes() {
        return new EntryType[]{EntryType.STRING};
    }


    @Override
    public String[] getSQLColumnNames() {
        return new String[] {getName()};
    }
}
