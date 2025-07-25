package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.pipes.ErrorPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public abstract class Producer<T> extends Filter {

    private Class<T> producingType;
    private final OutputPipe<T> outputPipe = new OutputPipe<>(this);
    private final ErrorPipe<T> errorPipe = new ErrorPipe<>(this);
    private final Class<?> classFromLambda;

    protected Producer(Class<?> classFromLambda)
    {
        this.classFromLambda = classFromLambda;
        this.producingType = null;
    }

    protected Producer(Class<T> producingType, Class<?> classFromLambda)
    {
        this.producingType = producingType;
        this.classFromLambda = classFromLambda;
    }

    @Override
    public String defaultPropertiesName()
    {
        return classFromLambda.getSimpleName().toLowerCase();
    }

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
    public ErrorPipe<T> errorPipe()
    {
        return errorPipe;
    }
}
