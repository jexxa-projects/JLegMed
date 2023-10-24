package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.persistence.RepositoryManager;
import io.jexxa.jlegmed.core.filter.Context;

public class RepositoryProcessor {

    public static <T extends AbstractAggregate<T, ?>> T persist(T data, Context context)
    {
        var repository = RepositoryManager.getRepository(data.getAggregateType(), data.getKeyFunction(), context.getProperties());
        repository.add(data);
        return data;
    }

    public static <T extends AbstractAggregate<T, ?>> T update(T data, Context context)
    {
        var repository = RepositoryManager.getRepository(data.getAggregateType(), data.getKeyFunction(), context.getProperties());
        repository.update(data);
        return data;
    }

    private RepositoryProcessor()
    {
        //Private constructor
    }
}
