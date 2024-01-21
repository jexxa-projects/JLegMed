package io.jexxa.jlegmed.plugins.persistence;

import java.util.function.Consumer;

public class JDBCOperation {
    public static  <T> Consumer<JDBCContext<T>> dropTable(Class<T> table){
        return dropTable(table.getSimpleName());
    }

    public static  <T> Consumer<JDBCContext<T>> dropTable(String table){
        return jdbcContext -> jdbcContext.jdbcConnection().createTableCommand().dropTableIfExists(table);
    }

    private JDBCOperation()
    {
        // Private constructor since we provide only static methods
    }
}
