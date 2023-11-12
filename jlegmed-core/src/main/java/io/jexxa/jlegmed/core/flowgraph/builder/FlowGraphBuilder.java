package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.FixedRateScheduler;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class FlowGraphBuilder {
    private final String flowGraphID;
    private final JLegMed jLegMed;

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

    public FixedRateBuilder every(int fixedRate, TimeUnit timeUnit)
    {
        return new FixedRateBuilder(new FixedRateScheduler(fixedRate, timeUnit), flowGraphID, jLegMed);
    }


    public static class FixedRateBuilder
    {
        private final FixedRateScheduler scheduler;
        private final String flowGraphID;
        private final JLegMed jLegMed;
        public FixedRateBuilder(FixedRateScheduler scheduler, String flowGraphID, JLegMed jLegMed)
        {
            this.scheduler = scheduler;
            this.flowGraphID = flowGraphID;
            this.jLegMed = jLegMed;
        }

        public <T> ProducerBuilder<T> receive(Class<T> expectedData)
        {
            var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());

            scheduler.register(flowGraph);

            jLegMed.addFlowGraph(flowGraph, scheduler);

            return new ProducerBuilder<>(flowGraph, expectedData);
        }
    }

}
