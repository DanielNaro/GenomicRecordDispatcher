import java.util.Objects;

public class HasUnmapped extends AbstractTag {
    private final boolean hasUnmapped;

    private static Rule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(Rule rule) {
        HasUnmapped.rule = rule;
    }

    public HasUnmapped(boolean hasUnmapped) {
        this.hasUnmapped = hasUnmapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasUnmapped)) return false;
        HasUnmapped that = (HasUnmapped) o;
        return hasUnmapped == that.hasUnmapped;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasUnmapped);
    }

    @Override
    public String[] getValuesForSQL() {
        if(hasUnmapped){
            return new String[]{"1"};
        } else {
            return new String[]{"0"};
        }
    }

    @Override
    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            HasUnmapped castedO = (HasUnmapped) o;
            return Boolean.compare(hasUnmapped, castedO.hasUnmapped);
        }
    }


}
