package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getJDBCConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

public class JDBCSessionPool {
    public static final JDBCSessionPool INSTANCE = new JDBCSessionPool();

    public static JDBCSession jdbcSession(FilterContext filterContext) {
        return new JDBCSession(getJDBCConnection(filterContext.properties(), INSTANCE));
    }

    private void initJDBCSessions(FilterProperties filterProperties)
    {
        try {
            if (filterProperties.properties().containsKey(jdbcUrl())) {
                getJDBCConnection(filterProperties.properties(), INSTANCE);
            }
        } catch ( RuntimeException e) {
            throw new FailFastException("Could not init JDBC connection for filter properties " + filterProperties.name()
                    + ". Reason: " + e.getMessage(), e );
        }
    }

    private JDBCSessionPool()
    {
        BootstrapRegistry.registerFailFastHandler(this::initJDBCSessions);
    }
}
