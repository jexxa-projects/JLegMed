package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.common.wrapper.utils.properties.PropertiesUtils;
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
    private final Filter predecessor;


    public ProcessorBinding(OutputPipe<T> predecessorPipe, Filter predecessor, FlowGraph<?> flowGraph)
    {
        this.predecessorPipe = predecessorPipe;
        this.predecessor = predecessor;
        this.flowGraph = flowGraph;
    }

    public <R> ProcessorBinding<R> andProcessWith(BiFunction<T, FilterContext, R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addFilter(successor);
        return new ProcessorBinding<>(successor.outputPipe(), successor,flowGraph);
    }

    public <R> ProcessorBinding<R> andProcessWith(Function<T,R> successorFunction)
    {
        var successor = processor(successorFunction);
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addFilter(successor);
        return new ProcessorBinding<>(successor.outputPipe(), successor, flowGraph);
    }

    public <R> ProcessorBinding<R> andProcessWith(Processor<T, R> successor)
    {
        predecessorPipe.connectTo(successor.inputPipe());

        flowGraph.addFilter(successor);
        return new ProcessorBinding<>(successor.outputPipe(), successor, flowGraph);
    }


    public <U> ProcessorBinding<T> filterConfig(U configuration)
    {
        predecessor.config(configuration);
        return this;
    }

    public ProcessorBinding<T> useProperties(FilterProperties filterProperties)
    {
        predecessor.properties(filterProperties);
        return this;
    }

    public ProcessorBinding<T> useProperties(String propertiesPrefix)
    {
        predecessor.properties(new FilterProperties(
                propertiesPrefix,
                PropertiesUtils.getSubset(flowGraph.getProperties(), propertiesPrefix))
        );

        return this;
    }

    public <U> ProcessorBinding<T> configureWith(FilterProperties filterProperties, U filterConfig)
    {
        filterConfig(filterConfig);
        return useProperties(filterProperties);
    }

    public <U> ProcessorBinding<T> configureWith(String propertiesPrefix, U filterConfig)
    {
        return configureWith(new FilterProperties(propertiesPrefix, PropertiesUtils.getSubset(flowGraph.getProperties(), propertiesPrefix)), filterConfig);
    }
}
