package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;

/**
 * This class represents a connection between two filters as a first-class object to the application.
 * The main purpose is to enable configuration of the used filters while ensuring a type safe connection.
 *
 * @param <T> Datatype send over this connection
 */
public class ProcessorBinding<T> {
    private final OutputPipe<T> predecessorPipe;
    private final FlowGraph<?> flowGraph;


    public ProcessorBinding(OutputPipe<T> predecessorPipe, FlowGraph<?> flowGraph)
    {
        this.predecessorPipe = predecessorPipe;
        this.flowGraph = flowGraph;
    }

    public <R> Binding<R> processWith(BiFunction<T, FilterContext, R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addFilter(successor);
        return new Binding<>(successor, successor.outputPipe(), flowGraph);
    }

    public <R> Binding<R> processWith(Function<T,R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addFilter(successor);
        return new Binding<>(successor, successor.outputPipe(), flowGraph);
    }

    public <R> Binding<R> processWith(Processor<T, R> successor)
    {
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addFilter(successor);
        return new Binding<>(successor, successor.outputPipe(), flowGraph);
    }
}
