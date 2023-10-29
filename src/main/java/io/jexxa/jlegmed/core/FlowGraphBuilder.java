package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.filter.ProducerBinding;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;

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

    public <T> ProducerBinding<T> await(Class<T> inputData) {
        var flowGraph = new FlowGraph<T>(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraphID, flowGraph);
        return new ProducerBinding<>(flowGraph, inputData);
    }

    public FlowGraphBuilder each(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedInterval = fixedRate;
        this.timeUnit = timeUnit;

        return this;
    }

    public <T> ProducerBinding<T> receive(Class<T> expectedData)
    {
        var eachFlowgraph = new ScheduledFlowGraph<T>(flowGraphID, jLegMed.getProperties(), fixedInterval, timeUnit);
        jLegMed.addFlowGraph(flowGraphID, eachFlowgraph);

        return new ProducerBinding<>(eachFlowgraph, expectedData);
    }
}
