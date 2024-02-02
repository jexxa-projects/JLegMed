package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

public class JDBCSessionPool {
    public static final JDBCSessionPool INSTANCE;
    static {
        INSTANCE = new JDBCSessionPool();
    }

    public static JDBCConnection jdbcConnection(FilterContext filterContext) {
        return getConnection(filterContext.properties(), INSTANCE);
    }
    private void initJDBCSessions(FilterProperties filterProperties)
    {
        if (filterProperties.properties().containsKey(jdbcUrl()))
        {
            getConnection(filterProperties.properties(), INSTANCE);
        }
    }
    private JDBCSessionPool()
    {
        BootstrapRegistry.registerFailFastHandler(this::initJDBCSessions);
    }

}
