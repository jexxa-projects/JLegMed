package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.processor.TypedProcessor;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class represents a connection between two filters as a first-class object to the application.
 * The main purpose is to enable configuration of the used filters while ensuring a type safe connection.
 *
 * @param <T> Datatype send over this connection
 */
public class Binding<T> {
    private final OutputPipe<T> predecessorPipe;
    private final TypedProcessor<?,?> predecessor;


    public Binding(OutputPipe<T> predecessorPipe, TypedProcessor<?,?> predecessor)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessor = predecessor;
    }

    public <R> Binding<R> andProcessWith(BiFunction<T, Context, R> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());
        return new Binding<>(successor.getOutputPipe(), successor);
    }

    public <R> Binding<R> andProcessWith(Function<T,R> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());

        return new Binding<>(successor.getOutputPipe(), successor);
    }


    public <U> Binding<T> useConfig(U configuration)
    {
        predecessor.setConfiguration(configuration);
        return this;
    }
}
