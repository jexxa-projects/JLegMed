package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.HashMap;
import java.util.function.Function;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getJDBCConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

@SuppressWarnings("java:S6548")
public class RepositoryPool {
    private static final RepositoryPool INSTANCE = new RepositoryPool();
    
    private final HashMap<String, Repository<?,?>> repositories = new HashMap<>();

    public static <T, K> Repository<T, K> getRepository(Class<T> aggregateClazz,
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
                filterContext.propertiesName(),
                repository -> new Repository<>(aggregateClazz, keyFunction, filterContext)
        );
        return (Repository<T, K>) repositories.get(filterContext.propertiesName());
    }

    private RepositoryPool()
    {
        BootstrapRegistry.registerBootstrapHandler(repositories::clear);
        BootstrapRegistry.registerFailFastHandler(this::initJDBCSessions);
    }


    private void initJDBCSessions(FilterProperties filterProperties)
    {
        try {
            if (filterProperties.properties().containsKey(jdbcUrl()))
            {
                getJDBCConnection(filterProperties.properties(), INSTANCE);
            }
        } catch ( RuntimeException e) {
        throw new FailFastException("Could not init JDBC connection for filter properties " + filterProperties.name()
                + ". Reason: " + e.getMessage(), e );
    }
    }

}
