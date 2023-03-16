package es.upc.dmag.dispatcher;

public abstract class AbstractTag implements Comparable<AbstractTag>{
    abstract Rule getRule();

    public abstract String[] getValuesForSQL();


}
