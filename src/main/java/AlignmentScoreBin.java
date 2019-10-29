import java.util.Objects;

public class AlignmentScoreBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        AlignmentScoreBin.rule = rule;
    }

    public AlignmentScoreBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlignmentScoreBin)) return false;
        AlignmentScoreBin that = (AlignmentScoreBin) o;
        return bin == that.bin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), bin);
    }

    @Override
    public String[] getValuesForSQL() {
        return rule.getValuesForSQL(bin);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            AlignmentScoreBin castedO = (AlignmentScoreBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
