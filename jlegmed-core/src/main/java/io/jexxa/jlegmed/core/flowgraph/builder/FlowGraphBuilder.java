package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class FlowGraphBuilder {

    private final FlowGraph flowGraph;

    public FlowGraphBuilder(String flowGraphID, JLegMed jLegMed)
    {
        this.flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraph);
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

}
