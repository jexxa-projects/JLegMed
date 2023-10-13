package io.jexxa.jlegmed.core.flowgraph;

public interface Producer {
    Object produce(Class<?> clazz, Context context);
}
