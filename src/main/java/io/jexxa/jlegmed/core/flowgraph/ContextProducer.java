package io.jexxa.jlegmed.core.flowgraph;

public interface ContextProducer {
    Object produce(Class<?> clazz, Context context);

}
