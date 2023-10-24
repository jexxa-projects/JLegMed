package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
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

    public <T> AwaitFlowGraphBuilder<T> await(Class<T> inputData) {
        var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraphID, flowGraph);
        return new AwaitFlowGraphBuilder<>(flowGraph, inputData);
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

    public static class AwaitFlowGraphBuilder<T>
    {
        private final FlowGraph flowGraph;
        private final Class<T> sourceType;

        AwaitFlowGraphBuilder(FlowGraph flowGraph, Class<T> sourceType)
        {
            this.sourceType = sourceType;
            this.flowGraph = flowGraph;
        }
        public <U extends TypedProducer<T>> U from(U producer) {
            try {
                flowGraph.setProducer(producer);
                producer.setContext(flowGraph.getContext());
                producer.setType(sourceType);

            } catch (Exception e){
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return producer;
        }

    }

}
