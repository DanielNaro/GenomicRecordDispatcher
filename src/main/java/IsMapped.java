import java.util.Objects;

public class IsMapped extends AbstractTag {
    private final boolean hasMapped;

    private static Rule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(Rule rule) {
        IsMapped.rule = rule;
    }

    public IsMapped(boolean hasMapped) {
        this.hasMapped = hasMapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IsMapped)) return false;
        IsMapped isMapped1 = (IsMapped) o;
        return hasMapped == isMapped1.hasMapped;
    }

    @Override
    public String[] getValuesForSQL() {
        if(hasMapped){
            return new String[]{"1"};
        } else {
            return new String[]{"0"};
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasMapped);
    }

    @Override
    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            IsMapped castedO = (IsMapped) o;
            return Boolean.compare(hasMapped, castedO.hasMapped);
        }
    }

}
