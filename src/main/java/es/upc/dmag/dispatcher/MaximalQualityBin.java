package es.upc.dmag.dispatcher;

import java.util.Objects;

public class MaximalQualityBin extends AbstractTag {
    private final int binId;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        MaximalQualityBin.rule = rule;
    }

    public MaximalQualityBin(int binId) {
        this.binId = binId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaximalQualityBin)) return false;
        MaximalQualityBin that = (MaximalQualityBin) o;
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
            MaximalQualityBin castedO = (MaximalQualityBin) o;
            return Integer.compare(binId, castedO.binId);
        }
    }
}
