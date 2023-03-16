package es.upc.dmag.dispatcher;

import java.util.Objects;

public class HasPairedEnded extends AbstractTag {
    private final boolean hasPairedEnded;

    private static Rule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(Rule rule) {
        HasPairedEnded.rule = rule;
    }

    public HasPairedEnded(boolean hasPairedEnded) {
        this.hasPairedEnded = hasPairedEnded;
    }


    @Override
    public String[] getValuesForSQL() {
        if(hasPairedEnded){
            return new String[]{"1"};
        } else {
            return new String[]{"0"};
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasPairedEnded)) return false;
        HasPairedEnded that = (HasPairedEnded) o;
        return hasPairedEnded == that.hasPairedEnded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasPairedEnded);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            HasPairedEnded castedO = (HasPairedEnded) o;
            return Boolean.compare(hasPairedEnded, castedO.hasPairedEnded);
        }
    }
}
