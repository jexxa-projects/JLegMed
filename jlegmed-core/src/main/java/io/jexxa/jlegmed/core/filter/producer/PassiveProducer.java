package io.jexxa.jlegmed.core.filter.producer;

public abstract class PassiveProducer<T> extends Producer<T> {
    protected  PassiveProducer(Class<?> classFromLambda) {
        super(classFromLambda);
    }

    protected  PassiveProducer(Class<T> sourceType,Class<?> classFromLambda) {
        super(sourceType, classFromLambda);
    }

    public abstract void produceData();
}
