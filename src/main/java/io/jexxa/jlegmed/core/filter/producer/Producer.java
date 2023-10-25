package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public interface Producer<T> {
    default void start() {}
    default void stop() {}
    OutputPipe<T> getOutputPipe();
    void setContext(Context context);
}
