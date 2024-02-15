package io.jexxa.jlegmed.core.filter.producer;

public abstract class PassiveProducer<T> extends Producer<T> {
    protected  PassiveProducer() {
        super();
    }

    protected  PassiveProducer(Class<T> sourceType) {
        super(sourceType);
    }

    public abstract void produceData();
}
