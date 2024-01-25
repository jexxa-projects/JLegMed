package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

@FunctionalInterface
public interface PipedProducer<T> {
    void produceData(FilterContext processContext, OutputPipe<T> outputPipe);
}
