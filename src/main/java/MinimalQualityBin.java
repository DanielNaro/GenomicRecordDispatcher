import java.util.Objects;

public class MinimalQualityBin extends AbstractTag{
    private final int binId;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        MinimalQualityBin.rule = rule;
    }

    public MinimalQualityBin(int binId) {
        this.binId = binId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinimalQualityBin)) return false;
        MinimalQualityBin that = (MinimalQualityBin) o;
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

    @Override
    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            MinimalQualityBin castedO = (MinimalQualityBin)o;
            return Integer.compare(binId, castedO.binId);
        }
    }
}
