package io.jexxa.jlegmed.core;

public interface Producer {
    <T> T produce(Class<T> clazz);
}
