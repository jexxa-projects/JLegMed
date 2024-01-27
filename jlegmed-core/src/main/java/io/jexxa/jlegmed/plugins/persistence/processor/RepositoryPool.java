package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("java:S6548")
public class RepositoryPool {

    public static final RepositoryPool INSTANCE = new RepositoryPool();
    HashMap<String, RepositoryProcessor<?,?>> repositories = new HashMap<>();

    public static <T, K> RepositoryProcessor<T, K> getRepository(Class<T> aggregateClazz,
                                                         Function<T,K> keyFunction, FilterContext filterContext)
    {
        return INSTANCE.getInternalRepository(aggregateClazz, keyFunction, filterContext);
    }


    @SuppressWarnings("unchecked") // OK, since the way we create the repository is type safe
    private <T, K> RepositoryProcessor<T, K> getInternalRepository(
            Class<T> aggregateClazz,
            Function<T,K> keyFunction,
            FilterContext filterContext)
    {
        repositories.computeIfAbsent(
                filterContext.propertiesName(),
                repository -> new RepositoryProcessor<>(aggregateClazz, keyFunction, filterContext)
        );
        return (RepositoryProcessor<T, K>) repositories.get(filterContext.propertiesName());
    }

}
