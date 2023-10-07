package io.jexxa.jlegmed.producer;

import io.jexxa.jlegmed.Context;

public interface ContextProducer {
    <T> T produce(Class<T> clazz, Context context);

}
