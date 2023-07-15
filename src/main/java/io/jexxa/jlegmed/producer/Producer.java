package io.jexxa.jlegmed.producer;

public interface Producer {
    <T> T receive(Class<T> clazz);
}
