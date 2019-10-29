import java.util.Objects;

public class InsertionsLengthBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        InsertionsLengthBin.rule = rule;
    }

    public InsertionsLengthBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InsertionsLengthBin)) return false;
        InsertionsLengthBin that = (InsertionsLengthBin) o;
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
            InsertionsLengthBin castedO = (InsertionsLengthBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
