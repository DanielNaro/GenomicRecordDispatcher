package es.upc.dmag.dispatcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class UtilsDatabase {
    static Set<String> getAvailableTables(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT t.name FROM " +
                "sqlite_master" +
                " t WHERE" +
                " t" +
                ".type == 'table'");
        Set<String> result = new HashSet<>();

        while (resultSet.next()){
            result.add(resultSet.getString(1));
        }
        return result;
    }

    static Set<String> getAvailableColumns(Statement statement) throws SQLException{
        ResultSet resultSet = statement.executeQuery("PRAGMA table_info('AUsMain')");
        Set<String> result = new HashSet<>();

        while (resultSet.next()){
            result.add(resultSet.getString(2));
        }
        return result;

    }
}
