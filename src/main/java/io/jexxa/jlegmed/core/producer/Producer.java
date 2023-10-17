package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;

public interface Producer {
    Object produce(Class<?> clazz, Context context);
}
