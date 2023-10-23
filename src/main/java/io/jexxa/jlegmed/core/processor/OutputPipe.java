package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Context;

public interface OutputPipe<T> {
    void forward(T content, Context context);
    void connectTo(InputPipe<T> inputPipe);
}
