package es.upc.dmag.dispatcher;

import java.util.Objects;

public class MismatchedLengthFractionBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        MismatchedLengthFractionBin.rule = rule;
    }

    public MismatchedLengthFractionBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MismatchedLengthFractionBin)) return false;
        MismatchedLengthFractionBin that = (MismatchedLengthFractionBin) o;
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

    @Override
    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            MismatchedLengthFractionBin castedO = (MismatchedLengthFractionBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
