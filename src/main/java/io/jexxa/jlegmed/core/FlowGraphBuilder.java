package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.ActiveFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;
import io.jexxa.jlegmed.core.producer.TypedProducer;

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

    public <T> ActiveFlowGraph<T> await(Class<T> inputData) {
        var flowGraph = new ActiveFlowGraph<>(flowGraphID, inputData, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraphID, flowGraph);
        return flowGraph;
    }

    public FlowGraphBuilder each(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedInterval = fixedRate;
        this.timeUnit = timeUnit;

        return this;
    }

    public <T> TypedProducer<T> receive(Class<T> expectedData)
    {
        var eachFlowgraph = new ScheduledFlowGraph<T>(flowGraphID, jLegMed.getProperties(), fixedInterval, timeUnit);
        jLegMed.addFlowGraph(flowGraphID, eachFlowgraph);
        return eachFlowgraph.receive(expectedData);
    }

}
