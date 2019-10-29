import java.util.Objects;

public abstract class AbstractTag implements Comparable<AbstractTag>{
    abstract Rule getRule();

    public abstract String[] getValuesForSQL();


}
