package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Context;

public interface InputPipe<T> {
    void receive(T content, Context context);
}
