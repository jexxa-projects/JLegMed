package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableBiConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.ManagedStreamProcessor;
import io.jexxa.jlegmed.core.filter.processor.PipedProcessor;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.processor.StreamProcessor;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import static io.jexxa.jlegmed.core.filter.processor.Processor.consumer;
import static io.jexxa.jlegmed.core.filter.processor.Processor.managedStreamProcessor;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.core.filter.processor.Processor.streamProcessor;

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

    /**
     * Links the current processing step to a subsequent function.
     * <p>
     * This method creates a new processor based on the provided {@code successorFunction},
     * connects the predecessor's output pipe to the new processor's input pipe,
     * and registers the new processor within the flow graph.
     * <strong>Note on Semantics:</strong> This method follows <b>1:1 semantics</b>, meaning
     * each input element {@code T} results in exactly one output element {@code R}.
     *  For operations that require 1:n semantics (where one input can produce multiple
     *  output elements), consider using {@link #streamWith(SerializableBiConsumer)}
     *
     *  @param <R>               The result type of the new processing step.
     * @param successorFunction A serializable bi-function that processes the current
     *                          element {@code T} and the {@link FilterContext}.
     * @return A new {@link Binding} representing the output and error pipes of the
     *         newly created processor for further chaining.
     */
    public <R> Binding<T, R> processWith(SerializableBiFunction<T, FilterContext, R> successorFunction) {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), successor.outputPipe(), flowGraph);
    }

    /**
     * Links the current processing step to a subsequent function.
     * <p>
     * This method creates a new processor based on the provided {@code successorFunction},
     * connects the predecessor's output pipe to the new processor's input pipe,
     * and registers the new processor within the flow graph.
     * <br>
     * <strong>Note on Semantics:</strong> This method follows <b>1:1 semantics</b>, meaning
     * each input element {@code T} results in exactly one output element {@code R}.
     *  For operations that require 1:n semantics (where one input can produce multiple
     *  output elements), consider using {@link #streamWith(SerializableBiConsumer)}
     *
     * @param <R>               The result type of the new processing step.
     * @param successorFunction A serializable function that processes the current
     *                          element {@code T} and the {@link FilterContext}.
     * @return A new {@link Binding} representing the output and error pipes of the
     *         newly created processor for further chaining.
     */
    public <R> Binding<T, R> processWith(SerializableFunction<T, R> successorFunction) {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), successor.outputPipe(), flowGraph);
    }



    @Deprecated(forRemoval = true)
    public <R> Binding<T, R> processWith(PipedProcessor<T, R> successorFunction) {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), successor.outputPipe(), flowGraph);
    }

    /**
     * Links the current processing step to a subsequent function using 1:n semantics.
     * <p>
     * This method creates a new processor that allows a single input element {@code T}
     * to produce multiple output elements of type {@code R}. It connects the
     * predecessor's output pipe to the new processor's input pipe and registers
     * the processor within the flow graph.
     * </p>
     * <p>
     * <strong>Note on Semantics:</strong> This method is designed for <b>1:n semantics</b>.
     * If you need to map a single input to exactly one output (1:1 semantics),
     * consider using {@link #processWith(SerializableBiFunction)} instead.
     * </p>
     *
     * @param <R>               The type of the resulting output elements.
     * @param successorFunction A PipedProcessor that processes the current
     *                          element {@code T} and the {@link FilterContext},
     *                          potentially producing multiple results.
     * @return A new {@link Binding} representing the output and error pipes of the
     *         newly created processor for further chaining.
     */
    public <R> Binding<T, R> streamWith(PipedProcessor<T, R> successorFunction) {
        var successor = managedStreamProcessor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), successor.outputPipe(), flowGraph);
    }

    /**
     * Links the current processing step to a subsequent function using 1:n semantics.
     * <p>
     * This method creates a new processor that allows a single input element {@code T}
     * to produce multiple output elements of type {@code R}. It connects the
     * predecessor's output pipe to the new processor's input pipe and registers
     * the processor within the flow graph.
     * </p>
     * <p>
     * <strong>Note on Semantics:</strong> This method is designed for <b>1:n semantics</b>.
     * If you need to map a single input to exactly one output (1:1 semantics),
     * consider using {@link #processWith(SerializableBiFunction)} instead.
     * </p>
     *
     * @param <R>               The type of the resulting output elements.
     * @param successorFunction A serializable bi-consumer that processes the current
     *                          element {@code T} and the {@link FilterContext},
     *                          potentially producing multiple results.
     * @return A new {@link Binding} representing the output and error pipes of the
     *         newly created processor for further chaining.
     */    public <R> Binding<T, R> streamWith(SerializableBiConsumer<T, OutputPipe<R>> successorFunction) {
        var successor = streamProcessor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), successor.outputPipe(), flowGraph);
    }

    /**
     * Links the current processing step to a subsequent function using 1:n semantics.
     * <p>
     * This method creates a new processor that allows a single input element {@code T}
     * to produce multiple output elements of type {@code R}. It connects the
     * predecessor's output pipe to the new processor's input pipe and registers
     * the processor within the flow graph.
     * </p>
     * <p>
     * <strong>Note on Semantics:</strong> This method is designed for <b>1:n semantics</b>.
     * If you need to map a single input to exactly one output (1:1 semantics),
     * consider using {@link #processWith(SerializableBiFunction)} instead.
     * </p>
     *
     * @param <R>               The type of the resulting output elements.
     * @param managedStreamProcessor A filter that uses current
     *                          element {@code T}, the {@link FilterContext}, and the output pipe,
     *                          potentially producing multiple results.
     * @return A new {@link Binding} representing the output and error pipes of the
     *         newly created processor for further chaining.
     */
    public <R> Binding<T, R> streamWith(ManagedStreamProcessor<T, R> managedStreamProcessor) {
        predecessorPipe.connectTo(managedStreamProcessor.inputPipe());

        flowGraph.addProcessor(managedStreamProcessor);
        return new Binding<>(managedStreamProcessor, managedStreamProcessor.errorPipe(), managedStreamProcessor.outputPipe(), flowGraph);
    }

    public <R> Binding<T, R> streamWith(StreamProcessor<T, R> streamProcessor) {
        predecessorPipe.connectTo(streamProcessor.inputPipe());

        flowGraph.addProcessor(streamProcessor);
        return new Binding<>(streamProcessor, streamProcessor.errorPipe(), streamProcessor.outputPipe(), flowGraph);
    }

    public Binding<T, Void> consumeWith(SerializableConsumer<T> successorFunction) {
        var successor = consumer(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), null, flowGraph);
    }

    public Binding<T, Void> consumeWith(SerializableBiConsumer<T, FilterContext> successorFunction) {
        var successor = consumer(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), null, flowGraph);
    }


    public <R> Binding<T, R> processWith(Processor<T, R> successor) {
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addProcessor(successor);
        return new Binding<>(successor, successor.errorPipe(), successor.outputPipe(), flowGraph);
    }

}
