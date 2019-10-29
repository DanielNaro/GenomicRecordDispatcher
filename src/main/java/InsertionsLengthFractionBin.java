import java.util.Objects;

public class InsertionsLengthFractionBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        InsertionsLengthFractionBin.rule = rule;
    }

    public InsertionsLengthFractionBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InsertionsLengthFractionBin)) return false;
        InsertionsLengthFractionBin that = (InsertionsLengthFractionBin) o;
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
            InsertionsLengthFractionBin castedO = (InsertionsLengthFractionBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
