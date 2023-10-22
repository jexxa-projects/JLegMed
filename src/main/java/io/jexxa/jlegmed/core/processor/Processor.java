package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Context;

public interface Processor<T> {
    void process(T content, Context context);
}
