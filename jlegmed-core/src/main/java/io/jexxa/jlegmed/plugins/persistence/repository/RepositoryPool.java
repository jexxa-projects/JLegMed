package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.HashMap;
import java.util.Properties;
import java.util.function.Function;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.validateJDBCConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

@SuppressWarnings("java:S6548")
public class RepositoryPool {
    private static final RepositoryPool INSTANCE = new RepositoryPool();
    
    private final HashMap<FilterContext, Repository<?,?>> repositories = new HashMap<>();

    public static synchronized <T, K> Repository<T, K> getRepository(Class<T> aggregateClazz,
                                                        Function<T,K> keyFunction, FilterContext filterContext)
    {
        return INSTANCE.getInternalRepository(aggregateClazz, keyFunction, filterContext);
    }


    @SuppressWarnings("unchecked") // OK, since the way we create the repository is type safe
    private <T, K> Repository<T, K> getInternalRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            FilterContext filterContext)
    {
        repositories.computeIfAbsent(
                filterContext,
                repository -> new Repository<>(aggregateClazz, keyFunction, filterContext)
        );
        return (Repository<T, K>) repositories.get(filterContext);
    }

    private RepositoryPool()
    {
        JexxaContext.registerCleanupHandler(repositories::clear);
        JexxaContext.registerValidationHandler(this::initJDBCSessions);
    }


    private void initJDBCSessions(Properties properties)
    {
        try {
            if (properties.containsKey(jdbcUrl()))
            {
                validateJDBCConnection(properties);
            }
        } catch ( RuntimeException e) {
        throw new FailFastException("Could not init JDBC connection for filter properties "
                + ". Reason: " + e.getMessage(), e );
    }
    }

}
