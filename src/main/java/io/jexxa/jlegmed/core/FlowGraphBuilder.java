package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.ActiveFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.SourceConnector;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;

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

    public <T> SourceConnector<T> await(Class<T> inputData) {
        var flowGraph = new ActiveFlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraphID, flowGraph);
        return new SourceConnector<>(inputData, flowGraph);
    }

    public FlowGraphBuilder each(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedInterval = fixedRate;
        this.timeUnit = timeUnit;

        return this;
    }

    public <T> ScheduledFlowGraphBuilder<T> receive(Class<T> expectedData)
    {
        var eachFlowgraph = new ScheduledFlowGraph<T>(flowGraphID, jLegMed.getProperties(), fixedInterval, timeUnit);
        jLegMed.addFlowGraph(flowGraphID, eachFlowgraph);
        eachFlowgraph.receive(expectedData);

        return new ScheduledFlowGraphBuilder<>(eachFlowgraph);
    }

    public static class ScheduledFlowGraphBuilder<T>
    {
        private final ScheduledFlowGraph<T> scheduledFlowGraph;

        ScheduledFlowGraphBuilder(ScheduledFlowGraph<T> scheduledFlowGraph)
        {
            this.scheduledFlowGraph = scheduledFlowGraph;
        }
        public <U extends TypedProducer<T>> U from(U producer)
        {
            return scheduledFlowGraph.from(producer);
        }

        public TypedProducer<T> generated()
        {
            return scheduledFlowGraph.getProducer();
        }
    }

}
