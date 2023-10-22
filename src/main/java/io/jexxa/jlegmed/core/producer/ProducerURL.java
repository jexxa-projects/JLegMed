package io.jexxa.jlegmed.core.producer;

public interface ProducerURL {

    <T> void init(TypedProducer<T> producer);
}
