package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.processor.OutputPipe;

public interface Producer<T> {
    void produce(Class<T> clazz, Context context);

    OutputPipe<T> getOutputPipe();
}
