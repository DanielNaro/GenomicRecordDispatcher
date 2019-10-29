import java.util.Objects;

public class NumInsertionsBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        NumInsertionsBin.rule = rule;
    }

    public NumInsertionsBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumInsertionsBin)) return false;
        NumInsertionsBin that = (NumInsertionsBin) o;
        return bin == that.bin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bin);
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
            NumInsertionsBin castedO = (NumInsertionsBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
