import java.util.Objects;

public class NumSplicesBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        NumSplicesBin.rule = rule;
    }

    public NumSplicesBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumSplicesBin)) return false;
        NumSplicesBin that = (NumSplicesBin) o;
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
            NumSplicesBin castedO = (NumSplicesBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
