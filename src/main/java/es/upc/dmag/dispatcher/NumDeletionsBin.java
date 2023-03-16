package es.upc.dmag.dispatcher;

import java.util.Objects;

public class NumDeletionsBin extends AbstractTag {
    private final int bin;

    private static BinRule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(BinRule rule) {
        NumDeletionsBin.rule = rule;
    }

    public NumDeletionsBin(int bin) {
        this.bin = bin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumDeletionsBin)) return false;
        NumDeletionsBin that = (NumDeletionsBin) o;
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
            NumDeletionsBin castedO = (NumDeletionsBin) o;
            return Integer.compare(bin, castedO.bin);
        }
    }
}
