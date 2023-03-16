package es.upc.dmag.dispatcher;

import java.util.Objects;

public class DeletionsLengthFractionBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        DeletionsLengthFractionBin.rule = rule;
    }

    public DeletionsLengthFractionBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeletionsLengthFractionBin)) return false;
        DeletionsLengthFractionBin that = (DeletionsLengthFractionBin) o;
        return bin == that.bin;
    }

    @Override
    public String[] getValuesForSQL() {
        return rule.getValuesForSQL(bin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bin);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            DeletionsLengthFractionBin castedO = (DeletionsLengthFractionBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
