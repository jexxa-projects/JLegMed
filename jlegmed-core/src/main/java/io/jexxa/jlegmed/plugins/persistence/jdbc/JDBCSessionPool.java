package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

public class JDBCSessionPool {
    public static final JDBCSessionPool INSTANCE = new JDBCSessionPool();
    private static boolean initialized = false;

    public static JDBCSession jdbcSession(FilterContext filterContext) {
        if (!initialized) {
            SLF4jLogger.getLogger(JDBCSessionPool.class).warn("JDBC session pool is not initialized. " +
                    "Please invoke JDBCSessionPool.init() in main");
        }
        return new JDBCSession(getConnection(filterContext.properties(), INSTANCE));
    }

    private void initJDBCSessions(FilterProperties filterProperties)
    {
        if (filterProperties.properties().containsKey(jdbcUrl()))
        {
            getConnection(filterProperties.properties(), INSTANCE);
        }
    }

    public static void init()
    {
        initialized = true;
    }

    private JDBCSessionPool()
    {
        BootstrapRegistry.registerFailFastHandler(this::initJDBCSessions);
    }

}
