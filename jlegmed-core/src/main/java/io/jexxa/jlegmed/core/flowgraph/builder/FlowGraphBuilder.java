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

    public <T> ActiveProducerBuilder<T> await(Class<T> inputData) {
        return new ActiveProducerBuilder<>(flowGraph, inputData);
    }

    public FixedRateSchedulerBuilder every(int fixedRate, TimeUnit timeUnit)
    {
        return new FixedRateSchedulerBuilder(flowGraph, fixedRate, timeUnit);
    }

    public RepeatSchedulerBuilder repeat(int times)
    {
        return new RepeatSchedulerBuilder(times, flowGraph);
    }

}
