package io.jexxa.jlegmed.core;

public interface ContextProducer {
    <T> T produce(Class<T> clazz, Context context);

}
