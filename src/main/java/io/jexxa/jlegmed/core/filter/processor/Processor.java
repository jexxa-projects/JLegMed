package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;

public interface Processor<T> {
    void process(T content, Context context);
}
