package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.adapterapi.ConfigurationFailedException;
import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Properties;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getJDBCConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

public class JDBCSessionPool {
    public static final JDBCSessionPool INSTANCE = new JDBCSessionPool();

    public static JDBCSession jdbcSession(FilterContext filterContext) {
        return new JDBCSession(getJDBCConnection(filterContext.properties(), filterContext));
    }

    public void initJDBCSessions(Properties properties)
    {
        try {
            if (properties.containsKey(jdbcUrl())) {
                getJDBCConnection(properties, INSTANCE);
            }
        } catch ( RuntimeException e) {
            throw new ConfigurationFailedException("Could not init JDBC connection for filter properties " + properties.getProperty(jdbcUrl())
                    + ". Reason: " + e.getMessage(), e );
        }
    }

    private JDBCSessionPool()
    {
        JexxaContext.registerValidationHandler(this::initJDBCSessions);
    }

}
