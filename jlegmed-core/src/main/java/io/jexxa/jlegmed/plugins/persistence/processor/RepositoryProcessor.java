package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.core.filter.FilterContext;

import static io.jexxa.commons.wrapper.component.persistence.RepositoryManager.getRepository;

public class RepositoryProcessor {

    public static <T extends AbstractAggregate<T, ?>> T persist(T data, FilterContext context)
    {
        var repository = getRepository(data.getAggregateType(), data.getKeyFunction(), context.properties());
        repository.add(data);
        return data;
    }

    public static <T extends AbstractAggregate<T, ?>> T update(T data, FilterContext context)
    {
        var repository = getRepository(data.getAggregateType(), data.getKeyFunction(), context.properties() );
        repository.update(data);
        return data;
    }

    private RepositoryProcessor()
    {
        //Private constructor
    }
}
