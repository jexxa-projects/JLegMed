package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.common.facade.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Properties;

public class JDBCSessionPool {
    public static final JDBCSessionPool INSTANCE = new JDBCSessionPool();
    public static JDBCConnection getJDBCConnection(FilterContext filterContext) {
        return JDBCConnectionPool.getConnection(filterContext.properties(), INSTANCE);
    }
    private void initJDBCSessions(Properties properties)
    {

    }
    private JDBCSessionPool()
    {
        BootstrapRegistry.registerInitHandler(this::initJDBCSessions);
    }

}
