package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
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
    private Processor<?,T> predecessorProcessor;
    private TypedProducer<T> predecessorProducer;


    public ProcessorBinding(OutputPipe<T> predecessorPipe, Processor<?,T> predecessor)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessorProcessor = predecessor;
    }

    public ProcessorBinding(OutputPipe<T> predecessorPipe, TypedProducer<T> predecessor)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessorProducer = predecessor;
    }


    public <R> ProcessorBinding<R> andProcessWith(BiFunction<T, Context, R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());
        return new ProcessorBinding<>(successor.getOutputPipe(), successor);
    }

    public <R> ProcessorBinding<R> andProcessWith(Function<T,R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());

        return new ProcessorBinding<>(successor.getOutputPipe(), successor);
    }

    public <R> ProcessorBinding<R> andProcessWith(Processor<T, R> successor)
    {
        predecessorPipe.connectTo(successor.getInputPipe());

        return new ProcessorBinding<>(successor.getOutputPipe(), successor);
    }


    public <U> ProcessorBinding<T> useConfig(U configuration)
    {
        if (predecessorProcessor != null)
        {
            predecessorProcessor.setConfiguration(configuration);
        }
        if (predecessorProducer != null)
        {
            predecessorProducer.setConfiguration(configuration);
        }

        return this;
    }
}
