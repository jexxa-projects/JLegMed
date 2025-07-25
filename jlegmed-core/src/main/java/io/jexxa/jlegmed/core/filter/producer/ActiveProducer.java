package io.jexxa.jlegmed.core.filter.producer;

public abstract class ActiveProducer<T> extends Producer<T> {
    protected ActiveProducer(Class<?> classFromLambda)
    {
        super(classFromLambda);
    }
}
