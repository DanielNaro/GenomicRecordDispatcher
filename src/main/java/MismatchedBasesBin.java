import java.util.Objects;

public class MismatchedBasesBin extends AbstractTag {
    private final int binId;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        MismatchedBasesBin.rule = rule;
    }

    public MismatchedBasesBin(int binId) {
        this.binId = binId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MismatchedBasesBin)) return false;
        MismatchedBasesBin that = (MismatchedBasesBin) o;
        return binId == that.binId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(binId);
    }

    @Override
    public String[] getValuesForSQL() {
        return rule.getValuesForSQL(binId);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            MismatchedBasesBin castedO = (MismatchedBasesBin) o;
            return Integer.compare(binId, castedO.binId);
        }
    }
}
