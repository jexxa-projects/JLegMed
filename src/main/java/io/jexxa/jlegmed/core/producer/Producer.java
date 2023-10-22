package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.processor.TypedOutputPipe;

public interface Producer<T> {
    void produce(Class<T> clazz, Context context);

    default TypedOutputPipe<T> getOutputPipe()
    {
        return new TypedOutputPipe<>();
    }
}
