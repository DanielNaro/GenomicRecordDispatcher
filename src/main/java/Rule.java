public interface Rule<T extends AbstractTag>{
    boolean returnsMultipleTags();
    T[] getTag(GlobalRecord globalRecord);
    String getName();
    String[] getSQLColumnNames();
    EntryType[] getTypes();
}
