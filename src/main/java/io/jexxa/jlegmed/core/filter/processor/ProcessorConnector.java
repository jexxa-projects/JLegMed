package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ProcessorConnector<T> {
    private final OutputPipe<T> predecessorPipe;
    private final TypedProcessor<?,?> predecessor;


    public ProcessorConnector(OutputPipe<T> predecessorPipe, TypedProcessor<?,?> predecessor)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessor = predecessor;
    }

    public <R> ProcessorConnector<R> andProcessWith(BiFunction<T, Context, R> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());
        return new ProcessorConnector<>(successor.getOutputPipe(), successor);
    }

    public <R> ProcessorConnector<R> andProcessWith(Function<T,R> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());

        return new ProcessorConnector<>(successor.getOutputPipe(), successor);
    }


    public <U> ProcessorConnector<T> useConfig(U configuration)
    {
        predecessor.setConfiguration(configuration);
        return this;
    }
}
