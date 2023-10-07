package io.jexxa.jlegmed.producer;

public interface Producer {
    <T> T produce(Class<T> clazz);
}
