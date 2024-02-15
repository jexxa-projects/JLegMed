package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public abstract class Producer<T> extends Filter {

    private Class<T> producingType;
    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public void producingType(Class<T> producingType)
    {
        this.producingType = producingType;
    }

    protected Class<T> producingType()
    {
        return producingType;
    }

    public OutputPipe<T> outputPipe()
    {
        return outputPipe;
    }
}
