package es.upc.dmag.dispatcher;

import java.util.Objects;

public class HasSingleEnded extends AbstractTag {
    private final boolean hasSingleEnded;

    private static Rule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    @Override
    public String[] getValuesForSQL() {
        if(hasSingleEnded){
            return new String[]{"1"};
        } else {
            return new String[]{"0"};
        }
    }

    public static void setRule(Rule rule) {
        HasSingleEnded.rule = rule;
    }

    public HasSingleEnded(boolean hasSingleEnded) {
        this.hasSingleEnded = hasSingleEnded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasSingleEnded)) return false;
        HasSingleEnded that = (HasSingleEnded) o;
        return hasSingleEnded == that.hasSingleEnded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasSingleEnded);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            HasSingleEnded castedO = (HasSingleEnded) o;
            return Boolean.compare(hasSingleEnded, castedO.hasSingleEnded);
        }
    }
}
