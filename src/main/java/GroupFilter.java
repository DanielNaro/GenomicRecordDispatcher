import htsjdk.samtools.SAMRecord;

import java.util.Set;

public class GroupFilter implements Filter {
    private final String group;
    private final boolean forAllReads;

    public GroupFilter(String group, boolean forAllReads) {
        this.group = group;
        this.forAllReads = forAllReads;
    }

    @Override
    public boolean matchesFilter(GlobalRecord record) {
        boolean trueForAllReads = true;
        boolean trueForOneRead = false;

        for(SAMRecord samRecord : record.getReads()){
            if(samRecord.getReadGroup().getId().equals(group)){
                trueForOneRead = true;
            } else {
                trueForAllReads = false;
            }
        }
        if(forAllReads){
            return trueForAllReads;
        } else {
            return trueForOneRead;
        }
    }

    public boolean returnsMultipleTags() {
        return true;
    }

    @Override
    public String getSQLFilter(Set<String> availableColumns, Set<String> availableTables) {
        if(availableTables.contains("GroupData")) {
            return "AUsMain.id == GroupData.au_id AND GroupData.GroupData = \"" + group + "\"";
        } else {
            return "";
        }
    }

    @Override
    public String getExtraTable() {
        return "GroupData";
    }
}
