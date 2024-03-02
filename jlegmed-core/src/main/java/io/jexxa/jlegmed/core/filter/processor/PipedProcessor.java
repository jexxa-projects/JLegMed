package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.io.Serializable;

@FunctionalInterface
public interface PipedProcessor<T, R> extends Serializable {
    void processData(T data, FilterContext processContext, OutputPipe<R> outputPipe);
}
