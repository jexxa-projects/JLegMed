package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.PoolRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("java:S6548")
public class RepositoryPool {

    public static final RepositoryPool INSTANCE = new RepositoryPool();
    HashMap<String, Repository<?,?>> repositories = new HashMap<>();

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
        PoolRegistry.registerInitHandler(properties -> repositories.clear());
    }

}
