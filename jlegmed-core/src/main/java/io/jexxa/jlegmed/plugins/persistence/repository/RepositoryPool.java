package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.HashMap;
import java.util.function.Function;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;

@SuppressWarnings("java:S6548")
public class RepositoryPool {
    private static boolean initialized = false;
    private static final RepositoryPool INSTANCE = new RepositoryPool();
    
    private final HashMap<String, Repository<?,?>> repositories = new HashMap<>();

    public static <T, K> Repository<T, K> getRepository(Class<T> aggregateClazz,
                                                        Function<T,K> keyFunction, FilterContext filterContext)
    {
        if (!initialized) {
            SLF4jLogger.getLogger(RepositoryPool.class).warn("RepositoryPool is not initialized. " +
                    "Please invoke RepositoryPool.init() in main");
        }
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

    public static void init()
    {
        initialized = true;
    }

    private RepositoryPool()
    {
        BootstrapRegistry.registerBootstrapHandler(repositories::clear);
        BootstrapRegistry.registerFailFastHandler(this::initJDBCSessions);
    }


    private void initJDBCSessions(FilterProperties filterProperties)
    {
        if (filterProperties.properties().containsKey(jdbcUrl()))
        {
            getConnection(filterProperties.properties(), INSTANCE);
        }
    }

}
