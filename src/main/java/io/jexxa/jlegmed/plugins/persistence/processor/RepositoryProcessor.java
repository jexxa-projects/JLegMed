package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.Properties;

import static io.jexxa.jlegmed.common.component.persistence.RepositoryManager.getRepository;

public class RepositoryProcessor {

    public static <T extends AbstractAggregate<T, ?>> T persist(T data, Context context)
    {
        var properties = new Properties();
        if (context.getFilterContext().filterProperties().isPresent())
        {
            properties = context.getFilterContext().filterProperties().orElseThrow().properties();
        }

        var repository = getRepository(data.getAggregateType(), data.getKeyFunction(), properties);
        repository.add(data);
        return data;
    }

    public static <T extends AbstractAggregate<T, ?>> T update(T data, Context context)
    {
        var properties = new Properties();
        if (context.getFilterContext().filterProperties().isPresent())
        {
            properties = context.getFilterContext().filterProperties().orElseThrow().properties();
        }

        var repository = getRepository(data.getAggregateType(), data.getKeyFunction(), properties );
        repository.update(data);
        return data;
    }

    private RepositoryProcessor()
    {
        //Private constructor
    }
}
