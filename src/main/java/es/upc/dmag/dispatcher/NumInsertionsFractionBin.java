package es.upc.dmag.dispatcher;

import java.util.Objects;

public class NumInsertionsFractionBin extends AbstractTag {
    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        NumInsertionsFractionBin.rule = rule;
    }

    private final int bin;

    public NumInsertionsFractionBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumInsertionsFractionBin)) return false;
        NumInsertionsFractionBin that = (NumInsertionsFractionBin) o;
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
            NumInsertionsFractionBin castedO = (NumInsertionsFractionBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
