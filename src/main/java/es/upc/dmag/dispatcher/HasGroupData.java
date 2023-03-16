package es.upc.dmag.dispatcher;

import java.util.Objects;

public class HasGroupData extends AbstractTag {
    private final String group;

    private static Rule rule;

    @Override
    Rule getRule() {
        return rule;
    }

    public static void setRule(Rule rule) {
        HasGroupData.rule = rule;
    }

    public HasGroupData(String group) {
        if(group == null){
            throw new IllegalArgumentException();
        }
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasGroupData)) return false;
        HasGroupData that = (HasGroupData) o;
        return group.equals(that.group);
    }

    @Override
    public String[] getValuesForSQL() {
        return new String[]{group};
    }

    @Override
    public int hashCode() {
        return Objects.hash(group);
    }

    public int compareTo(AbstractTag o) {
        if(getClass() != o.getClass()){
            return Integer.compare(getClass().hashCode(),
                    o.getClass().hashCode());
        } else {
            HasGroupData castedO = (HasGroupData) o;
            return group.compareTo(castedO.group);
        }
    }
}
