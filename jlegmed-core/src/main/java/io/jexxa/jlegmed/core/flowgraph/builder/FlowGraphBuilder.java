package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class FlowGraphBuilder {

    private final FlowGraph flowGraph;

    public FlowGraphBuilder(String flowGraphID, Properties properties, boolean enableStrictFailFast)
    {
        this.flowGraph = new FlowGraph(flowGraphID, properties);
        this.flowGraph.strictFailFast(enableStrictFailFast);
    }

    public FlowGraphBuilder enableStrictFailFast()
    {
        this.flowGraph.enableStrictFailFast();
        return this;
    }

    public FlowGraphBuilder disableStrictFailFast()
    {
        this.flowGraph.disableStrictFailFast();
        return this;
    }

    public <T> AwaitBuilder<T> await(Class<T> inputData) {
        return new AwaitBuilder<>(flowGraph, inputData);
    }

    public EveryBuilder every(int fixedRate, TimeUnit timeUnit)
    {
        return new EveryBuilder(flowGraph, fixedRate, timeUnit);
    }

    public RepeatBuilder repeat(int times)
    {
        return new RepeatBuilder(times, flowGraph);
    }

    public FlowGraph getFlowGraph()
    {
        return flowGraph;
    }

}
