import java.util.List;
import java.util.Set;

public class AndFilterCollection {
    private final List<Filter> filters;

    public AndFilterCollection(List<Filter> filters) {
        this.filters = filters;
    }

    public boolean matches(GlobalRecord record){
        for(Filter filter : filters){
            if(!filter.matchesFilter(record)){
                return false;
            }
        }
        return true;
    }

    public String generateSelect(Set<String> availableTables, Set<String> availableColumns){
        StringBuilder tables = new StringBuilder();
        tables.append("AUsMain");
        for(Filter filter : filters){
            String tablesForFilter = filter.getExtraTable();
            if(availableTables.contains(tablesForFilter)){
                if(tablesForFilter.length() != 0){
                    tables.append(",\n").append(tablesForFilter);
                }
            }
        }

        StringBuilder where = new StringBuilder();
        boolean first = true;
        for(Filter filter : filters){
            if(!filter.getSQLFilter(availableColumns, availableTables).equals("") ) {
                if (first) {
                    first = false;
                } else {
                    where.append(" AND ");
                }
                where.append(filter.getSQLFilter(availableColumns, availableTables)).append("\n");
            }
        }
        StringBuilder selectBuilder = new StringBuilder(
                "select distinct AUsMain.id, AUsMain.startData, AUsMain.endData, AUsMain.encrypted\n from "+tables
        );
        if(where.toString().equals("")){
            return selectBuilder.append(";").toString();
        } else {
            return selectBuilder.append(" where ").append(where).append(";").toString();
        }
    }
}
