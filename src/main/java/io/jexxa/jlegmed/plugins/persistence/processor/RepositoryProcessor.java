package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.persistence.RepositoryManager;
import io.jexxa.jlegmed.core.filter.Context;

public class RepositoryProcessor {

    public static <T extends AbstractAggregate<T, ?>> T persist(T data, Context context)
    {
        var processorfConfig = context.getFilterConfig(RepositoryConfiguration.class);
        var properties = context.getProperties(processorfConfig.propertiesPrefix());

        var repository = RepositoryManager.getRepository(data.getAggregateType(), data.getKeyFunction(), properties);
        repository.add(data);
        return data;
    }

    public static <T extends AbstractAggregate<T, ?>> T update(T data, Context context)
    {
        var repository = RepositoryManager.getRepository(data.getAggregateType(), data.getKeyFunction(), context.getProperties());
        repository.update(data);
        return data;
    }

    public record RepositoryConfiguration(String propertiesPrefix)
    {
        public static RepositoryConfiguration repositoryOf(String propertiesPrefix) { return new RepositoryConfiguration(propertiesPrefix);}
    }

    private RepositoryProcessor()
    {
        //Private constructor
    }
}
