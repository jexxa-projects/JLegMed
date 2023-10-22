package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.processor.TypedOutputPipe;

public interface Producer {
    void produce(Class<?> clazz, Context context);

    default <T> TypedOutputPipe<T> getOutputPipe()
    {
        return new TypedOutputPipe<>();
    }
}
