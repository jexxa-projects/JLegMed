package io.jexxa.jlegmed.core.builder;

import io.jexxa.jlegmed.core.JLegMed;
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

    public <T> ProducerBuilder<T> await(Class<T> inputData) {
        var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraphID, flowGraph);
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
        var eachFlowgraph = new ScheduledFlowGraph(flowGraphID, jLegMed.getProperties(), fixedInterval, timeUnit);
        jLegMed.addFlowGraph(flowGraphID, eachFlowgraph);

        return new ProducerBuilder<>(eachFlowgraph, expectedData);
    }

}
