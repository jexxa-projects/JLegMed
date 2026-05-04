package io.jexxa.jlegmed.core.filter.producer;

public abstract class ActiveProducer<T> extends Producer<T, ActiveProducer<T>> {
    protected ActiveProducer(Class<?> classFromLambda)
    {
        super(classFromLambda);
    }
    @Override
    protected ActiveProducer<T> self() {
        return this;
    }
}
