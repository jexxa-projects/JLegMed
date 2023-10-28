package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Optional;
import java.util.Properties;

public abstract class Producer<T> extends Filter {

    private Class<T> producingType;
    private Context context;
    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public void setType(Class<T> producingType)
    {
        this.producingType = producingType;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }


    protected Class<T> getType()
    {
        return producingType;
    }

    public OutputPipe<T> getOutputPipe()
    {
        return outputPipe;
    }

    protected Context getContext()
    {
        return context;
    }

    protected <R> Optional<R> getFilterConfig(Class<R> configType)
    {
        try {
            return Optional.ofNullable(getFilterConfig().getConfig(configType));
        } catch (ClassCastException e)
        {
            return Optional.empty();
        }
    }

    protected Optional<Properties> getFilterProperties()
    {
        if ( getFilterConfig().getPropertiesConfig() == null)
        {
            return Optional.empty();
        }
        return context.getProperties(getFilterConfig().getPropertiesConfig().properties());
    }
}
