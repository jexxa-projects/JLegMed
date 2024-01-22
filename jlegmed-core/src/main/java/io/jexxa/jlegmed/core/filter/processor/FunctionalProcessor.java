package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

@FunctionalInterface
public interface FunctionalProcessor<T, R> {
    void processData(T data, FilterContext processContext, OutputPipe<R> outputPipe);
}
