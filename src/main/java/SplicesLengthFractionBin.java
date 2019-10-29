import java.util.Objects;

public class SplicesLengthFractionBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        SplicesLengthFractionBin.rule = rule;
    }

    public SplicesLengthFractionBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplicesLengthFractionBin)) return false;
        SplicesLengthFractionBin that = (SplicesLengthFractionBin) o;
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
            SplicesLengthFractionBin castedO = (SplicesLengthFractionBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
