package io.jexxa.jlegmed.core;

public interface ContextProducer {
    Object produce(Class<?> clazz, Context context);

}
