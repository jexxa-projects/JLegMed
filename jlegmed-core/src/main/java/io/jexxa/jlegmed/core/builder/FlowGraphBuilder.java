package io.jexxa.jlegmed.core.builder;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.FixedRateScheduler;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class FlowGraphBuilder {
    private final String flowGraphID;
    private final JLegMed jLegMed;
    private int fixedInterval;
    private TimeUnit timeUnit;

    public FlowGraphBuilder(String flowGraphID, JLegMed jLegMed)
    {
        this.flowGraphID = flowGraphID;
        this.jLegMed = jLegMed;
    }

    public <T> ProducerBuilder<T> await(Class<T> inputData) {
        var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraph);
        return new ProducerBuilder<>(flowGraph, inputData);
    }

    public FlowGraphBuilder every(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedInterval = fixedRate;
        this.timeUnit = timeUnit;

        return this;
    }

    public <T> ProducerBuilder<T> receive(Class<T> expectedData)
    {
        var scheduler = new FixedRateScheduler(fixedInterval, timeUnit);
        var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());

        scheduler.register(flowGraph);

        jLegMed.addFlowGraph(flowGraph, scheduler);

        return new ProducerBuilder<>(flowGraph, expectedData);
    }

}
