import java.util.Objects;

public class HasOpticalDuplicate extends AbstractTag {
    private final boolean hasOpticalDuplicate;

    private static Rule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(Rule rule) {
        HasOpticalDuplicate.rule = rule;
    }

    public HasOpticalDuplicate(boolean hasOpticalDuplicate) {
        this.hasOpticalDuplicate = hasOpticalDuplicate;
    }

    @Override
    public String[] getValuesForSQL() {
        if(hasOpticalDuplicate){
            return new String[]{"1"};
        } else {
            return new String[]{"0"};
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasOpticalDuplicate)) return false;
        HasOpticalDuplicate that = (HasOpticalDuplicate) o;
        return hasOpticalDuplicate == that.hasOpticalDuplicate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasOpticalDuplicate);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            HasOpticalDuplicate castedO = (HasOpticalDuplicate) o;
            return Boolean.compare(hasOpticalDuplicate, castedO.hasOpticalDuplicate);
        }
    }
}
