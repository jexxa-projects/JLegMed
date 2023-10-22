package io.jexxa.jlegmed.core.flowgraph;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedConnector<T> {
    private final AbstractFlowGraph<?> flowGraph;


    public TypedConnector(AbstractFlowGraph<?> flowGraph)
    {
        this.flowGraph = flowGraph;
    }

    public <R> TypedConnector<R> andProcessWith(BiFunction<T, Context, R> successorFunction)
    {
        flowGraph.andProcessWith(successorFunction);
        return new TypedConnector<>(flowGraph);
    }

    public <R> TypedConnector<R> andProcessWith(Function<T,R> successorFunction)
    {
        flowGraph.andProcessWith(successorFunction);
        return new TypedConnector<>(flowGraph);
    }

    public <U> TypedConnector<T> useConfig(U configuration)
    {
        flowGraph.useConfig(configuration);
        return this;
    }


}
