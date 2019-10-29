import java.util.Objects;

public class MismatchedLengthBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        MismatchedLengthBin.rule = rule;
    }

    public MismatchedLengthBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MismatchedLengthBin)) return false;
        MismatchedLengthBin that = (MismatchedLengthBin) o;
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
            MismatchedLengthBin castedO = (MismatchedLengthBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
