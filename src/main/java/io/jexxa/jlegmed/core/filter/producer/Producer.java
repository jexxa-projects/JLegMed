package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Objects;
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

    @Override
    public void init()
    {
        Objects.requireNonNull(context);
        Objects.requireNonNull(producingType);
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

    protected Optional<Properties> getFilterProperties()
    {
        if ( getFilterConfig().getPropertiesConfig() == null)
        {
            return Optional.empty();
        }
        return context.getProperties(getFilterConfig().getPropertiesConfig().properties());
    }
}
