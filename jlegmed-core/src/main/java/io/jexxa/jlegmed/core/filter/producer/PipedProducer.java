package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.io.Serializable;

@FunctionalInterface
public interface PipedProducer<T> extends Serializable {
    void produceData(FilterContext processContext, OutputPipe<T> outputPipe);
}
