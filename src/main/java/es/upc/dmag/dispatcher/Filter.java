package es.upc.dmag.dispatcher;

import java.util.Set;

public interface Filter {
    boolean matchesFilter(GlobalRecord record);
    boolean returnsMultipleTags();
    String getSQLFilter(Set<String> availableColumns, Set<String> availableTables);
    String getExtraTable();

    default String generateRangeIntersect(String minRange1, String maxRange1, String minRange2, String maxRange2){
        return
                "(\n"+
                "\t\t("+minRange1 +">="+ minRange2 + " AND " +minRange1+ "<" +maxRange2+") OR\n"+
                "\t\t("+maxRange1 + ">" + minRange2 +" AND " + maxRange1 +"<"+ maxRange2+") OR\n" +
                "\t\t("+minRange2 + ">=" + minRange1 +" AND " + minRange2 +"<"+ maxRange1+") OR\n" +
                "\t\t("+maxRange2 + ">" + minRange1 +" AND " + maxRange2 +"<"+ maxRange1+")\n"+
                "\t)";
    }
}
