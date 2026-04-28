package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.adapterapi.ConfigurationFailedException;
import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.common.drivenadapter.persistence.repository.jdbc.JDBCKeyValueRepository;
import io.jexxa.common.drivenadapter.persistence.repository.s3.S3KeyValueRepository;
import io.jexxa.common.facade.s3.S3Client;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.HashMap;
import java.util.Properties;
import java.util.function.Function;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.validateJDBCConnection;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;
import static io.jexxa.common.facade.jdbc.JDBCProperties.repositoryStrategy;
import static io.jexxa.common.facade.s3.S3Properties.s3Endpoint;

@SuppressWarnings("java:S6548")
public class RepositoryPool {
    private static final RepositoryPool INSTANCE = new RepositoryPool();
    
    private final HashMap<FilterContext, Repository<?,?>> repositories = new HashMap<>();
    private final HashMap<FilterContext, ExpiringRepository<?,?>> expiringRepositories = new HashMap<>();

    public static synchronized <T, K> Repository<T, K> getRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            FilterContext filterContext)
    {
        return INSTANCE.getInternalRepository(aggregateClazz, keyFunction, aggregateClazz.getSimpleName(), filterContext);
    }

    public static synchronized <T, K> Repository<T, K> getRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            String storageName,
            FilterContext filterContext)
    {
        return INSTANCE.getInternalRepository(aggregateClazz, keyFunction, storageName, filterContext);
    }

    public static synchronized <T, K> ExpiringRepository<T, K> getExpiringRepository(
            Class<T> aggregateClazz,
            Class<K> keyClass,
            Function<T,K> keyFunction,
            String storageName,
            FilterContext filterContext)
    {
        return INSTANCE.getInternalExpiringRepository(
                aggregateClazz,
                keyClass,
                keyFunction,
                storageName,
                filterContext);
    }
    public static synchronized <T, K> ExpiringRepository<T, K> getExpiringRepository(
            Class<T> aggregateClazz,
            Class<K> keyClazz,
            Function<T,K> keyFunction,
            FilterContext filterContext)
    {
        return getExpiringRepository(
                aggregateClazz, keyClazz,
                keyFunction,
                aggregateClazz.getSimpleName(),
                filterContext);
    }


    @SuppressWarnings("unchecked") // OK, since the way we create the repository is type safe
    private <T, K> Repository<T, K> getInternalRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            String storageName,
            FilterContext filterContext)
    {
        repositories.computeIfAbsent(
                filterContext,
                _ -> new Repository<>(
                        aggregateClazz,
                        keyFunction, storageName,
                        filterContext)
        );
        return (Repository<T, K>) repositories.get(filterContext);
    }

    @SuppressWarnings("unchecked")
    private <T, K> ExpiringRepository<T, K> getInternalExpiringRepository(
            Class<T> aggregateClazz,
            Class<K> keyClazz,
            Function<T,K> keyFunction,
            String storageName,
            FilterContext filterContext)
    {
        expiringRepositories.computeIfAbsent(
                filterContext,
                _ -> new ExpiringRepository<>(
                        aggregateClazz,
                        keyClazz,
                        keyFunction,
                        storageName,
                        filterContext)
        );
        return (ExpiringRepository<T, K>) expiringRepositories.get(filterContext);
    }
    private RepositoryPool()
    {
        JexxaContext.registerCleanupHandler(repositories::clear);
        JexxaContext.registerValidationHandler(this::initJDBCSessions);
        JexxaContext.registerValidationHandler(this::initS3Sessions);
    }


    private void initS3Sessions(Properties properties)
    {
        if (properties.containsKey(repositoryStrategy()))
        {
            if (!properties.getProperty(repositoryStrategy()).equals(S3KeyValueRepository.class.getName()))
            {
                return;
            }
        }

        try {
            if (properties.containsKey(s3Endpoint()))
            {
                new S3Client(properties);
            }
        } catch ( RuntimeException e) {
        throw new ConfigurationFailedException("Could not init S3 connection for filter properties "
                + ". Reason: " + e.getMessage(), e );
        }
    }

    private void initJDBCSessions(Properties properties)
    {
        if (properties.containsKey(repositoryStrategy()))
        {
            if (!properties.getProperty(repositoryStrategy()).equals(JDBCKeyValueRepository.class.getName()))
            {
                return;
            }
        }

        try {
            if (properties.containsKey(jdbcUrl()))
            {
                validateJDBCConnection(properties);
            }
        } catch ( RuntimeException e) {
            throw new ConfigurationFailedException("Could not init JDBC connection for filter properties "
                    + ". Reason: " + e.getMessage(), e );
        }
    }

}
