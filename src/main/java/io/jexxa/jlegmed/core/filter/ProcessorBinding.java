package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.Producer;
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
    private Processor<?,T> predecessorProcessor;
    private Producer<T> predecessorProducer;


    public ProcessorBinding(OutputPipe<T> predecessorPipe, Processor<?,T> predecessor, FlowGraph<?> flowGraph)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessorProcessor = predecessor;
        this.flowGraph = flowGraph;
    }

    public ProcessorBinding(OutputPipe<T> predecessorPipe, Producer<T> predecessor, FlowGraph<?> flowGraph)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessorProducer = predecessor;
        this.flowGraph = flowGraph;
    }


    public <R> ProcessorBinding<R> andProcessWith(BiFunction<T, Context, R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());

        flowGraph.addFilter(successor);
        return new ProcessorBinding<>(successor.getOutputPipe(), successor,flowGraph);
    }

    public <R> ProcessorBinding<R> andProcessWith(Function<T,R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.getInputPipe());

        flowGraph.addFilter(successor);
        return new ProcessorBinding<>(successor.getOutputPipe(), successor, flowGraph);
    }

    public <R> ProcessorBinding<R> andProcessWith(Processor<T, R> successor)
    {
        predecessorPipe.connectTo(successor.getInputPipe());

        flowGraph.addFilter(successor);
        return new ProcessorBinding<>(successor.getOutputPipe(), successor, flowGraph);
    }


    public <U> ProcessorBinding<T> filterConfig(U configuration)
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

    public ProcessorBinding<T> useProperties(PropertiesConfig propertiesConfig)
    {
        if (predecessorProcessor != null)
        {
            predecessorProcessor.setProperties(propertiesConfig);
        }
        if (predecessorProducer != null)
        {
            predecessorProducer.setProperties(propertiesConfig);
        }

        return this;
    }

    public ProcessorBinding<T> useProperties(String propertiesPrefix)
    {
        if (predecessorProcessor != null)
        {
            predecessorProcessor.setProperties(new PropertiesConfig(propertiesPrefix));
        }
        if (predecessorProducer != null)
        {
            predecessorProducer.setProperties(new PropertiesConfig(propertiesPrefix));
        }

        return this;
    }

    public <U> ProcessorBinding<T> configureWith(PropertiesConfig propertiesConfig, U filterConfig)
    {
        filterConfig(filterConfig);
        return useProperties(propertiesConfig);
    }

    public <U> ProcessorBinding<T> configureWith(String propertiesPrefix, U filterConfig)
    {
        return configureWith(new PropertiesConfig(propertiesPrefix), filterConfig);
    }
}
