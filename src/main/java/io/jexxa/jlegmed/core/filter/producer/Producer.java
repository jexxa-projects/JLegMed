package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Objects;

public abstract class Producer<T> extends Filter {

    private Class<T> producingType;

    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public void setType(Class<T> producingType)
    {
        this.producingType = producingType;
    }

    @Override
    public void init()
    {
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


}
