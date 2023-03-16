package es.upc.dmag.dispatcher;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseCreator {
    public static Connection createDB(
            String fileName,
            Collection<Rule> rules
    ){
        List<Rule> singleTagRules = new ArrayList<>();
        List<Rule> multipleTagRules = new ArrayList<>();

        for(Rule rule : rules){
            if(rule.returnsMultipleTags()){
                multipleTagRules.add(rule);
            } else {
                singleTagRules.add(rule);
            }
        }

        Connection connection = null;
        try
        {
            // create a database connection
            String connectionName = "jdbc:sqlite:"+fileName;
            System.out.println("Connection to "+connectionName);
            connection = DriverManager.getConnection(connectionName);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            for(Rule rule : multipleTagRules){
                statement.executeUpdate("drop table if exists "+rule.getName().toLowerCase());
            }

            statement.executeUpdate("drop table if exists AUsMain");

            StringBuilder columnsMainTable = new StringBuilder();
            columnsMainTable.append('(');
            columnsMainTable.append("id integer primary key, startData integer,");
            columnsMainTable.append("endData integer, encrypted Boolean");

            for(Rule rule : singleTagRules){
                for(String columnName : rule.getSQLColumnNames()){
                    columnsMainTable.append(", ");
                    columnsMainTable.append(columnName).append(' ');
                    switch (rule.getTypes()[0]){
                        case BOOLEAN:
                            columnsMainTable.append("Boolean");
                            break;
                        case INTEGER:
                            columnsMainTable.append("integer");
                            break;
                        case STRING:
                            columnsMainTable.append("text");
                            break;
                    }
                }


            }
            columnsMainTable.append(')');
            statement.executeUpdate("create table AUsMain "+columnsMainTable.toString());

            for(Rule rule : multipleTagRules){
                StringBuilder commandCreationTable = new StringBuilder();
                commandCreationTable.append("create table ").append(rule.getName()).append(" (au_id integer, ");

                int column_i = 0;
                for(String columnName : rule.getSQLColumnNames()) {
                    if(column_i != 0){
                        commandCreationTable.append(",");
                    }
                    commandCreationTable.append(columnName).append(' ');
                    switch (rule.getTypes()[column_i]) {
                        case BOOLEAN:
                            commandCreationTable.append("integer");
                            break;
                        case INTEGER:
                            commandCreationTable.append("integer");
                            break;
                        case STRING:
                            commandCreationTable.append("text");
                            break;
                        case DOUBLE:
                            commandCreationTable.append("real");
                    }

                    column_i++;
                }
                commandCreationTable.append(", FOREIGN KEY (au_id) REFERENCES AUsMain(id))");
                statement.executeUpdate(commandCreationTable.toString());
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        return connection;
    }
}
