package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.core.filter.FilterContext;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;

public class JDBCOperation {
    public static  <T> T dropTable(FilterContext filterContext, Class<T> table){
        return dropTable(filterContext, table.getSimpleName());
    }

    public static  <T> T dropTable(FilterContext filterContext, String table){
        getConnection(filterContext.properties(), filterContext).createTableCommand().dropTableIfExists(table);
        return null;
    }

    private JDBCOperation()
    {
        // Private constructor since we provide only static methods
    }
}
