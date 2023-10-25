package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.FilterConfig;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Optional;

public abstract class Producer<T> {

    private Class<T> producingType;
    private Context context;
    private final FilterConfig filterConfig = new FilterConfig();

    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public abstract void start();

    public abstract void stop();

    public void setType(Class<T> producingType)
    {
        this.producingType = producingType;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
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

    protected FilterConfig getFilterConfig()
    {
        return filterConfig;
    }

    protected <R> Optional<R> getFilterConfig(Class<R> configType)
    {
        try {
            return Optional.ofNullable(filterConfig.getConfig(configType));
        } catch (ClassCastException e)
        {
            return Optional.empty();
        }
    }

}
