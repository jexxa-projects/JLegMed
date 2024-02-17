package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableBiConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.PipedProcessor;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import static io.jexxa.jlegmed.core.filter.processor.Processor.consumer;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;

/**
 * This class represents a connection between two filters as a first-class object to the application.
 * The main purpose is to enable configuration of the used filters while ensuring a type safe connection.
 *
 * @param <T> Datatype send over this connection
 */
public class ProcessorBuilder<T> {
    private final OutputPipe<T> predecessorPipe;
    private final FlowGraph flowGraph;


    ProcessorBuilder(OutputPipe<T> predecessorPipe, FlowGraph flowGraph) {
        this.predecessorPipe = predecessorPipe;
        this.flowGraph = flowGraph;
    }

    public <R> Binding<R, T> processWith(SerializableBiFunction<T, FilterContext, R> successorFunction) {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.outputPipe(), successor.errorPipe(), flowGraph);
    }

    public <R> Binding<R, T> processWith(SerializableFunction<T, R> successorFunction) {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.outputPipe(), successor.errorPipe(), flowGraph);
    }


    public <R> Binding<R, T> processWith(PipedProcessor<T, R> successorFunction) {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.outputPipe(), successor.errorPipe(), flowGraph);
    }

    public Binding<Void, T> consumeWith(SerializableConsumer<T> successorFunction) {
        var successor = consumer(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, null, successor.errorPipe(), flowGraph);
    }

    public Binding<Void, T> consumeWith(SerializableBiConsumer<T, FilterContext> successorFunction) {
        var successor = consumer(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, null, successor.errorPipe(), flowGraph);
    }


    public <R> Binding<R, T> processWith(Processor<T, R> successor) {
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.outputPipe(), successor.errorPipe(), flowGraph);
    }
}
