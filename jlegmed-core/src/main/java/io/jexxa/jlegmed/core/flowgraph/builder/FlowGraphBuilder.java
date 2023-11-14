package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.FixedRateScheduler;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.RepeatScheduler;

import java.util.concurrent.TimeUnit;

public class FlowGraphBuilder {
    private final String flowGraphID;
    private final JLegMed jLegMed;

    public FlowGraphBuilder(String flowGraphID, JLegMed jLegMed)
    {
        this.flowGraphID = flowGraphID;
        this.jLegMed = jLegMed;
    }

    public <T> ActiveProducerBuilder<T> await(Class<T> inputData) {
        var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addFlowGraph(flowGraph);
        return new ActiveProducerBuilder<>(flowGraph, inputData);
    }

    public FixedRateBuilder every(int fixedRate, TimeUnit timeUnit)
    {
        return new FixedRateBuilder(new FixedRateScheduler(fixedRate, timeUnit), flowGraphID, jLegMed);
    }

    public RepeatBuilder repeat(int times)
    {
        return new RepeatBuilder(times, flowGraphID, jLegMed);
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

    public static class RepeatBuilder
    {
        private final int times;
        private RepeatScheduler repeatScheduler;
        private final String flowGraphID;
        private final JLegMed jLegMed;
        public RepeatBuilder(int times, String flowGraphID, JLegMed jLegMed)
        {
            this.repeatScheduler = new RepeatScheduler(times);
            this.times = times;
            this.flowGraphID = flowGraphID;
            this.jLegMed = jLegMed;
        }

        public RepeatBuilder atInterval(int fixedRate, TimeUnit timeUnit)
        {
            this.repeatScheduler = new RepeatScheduler(times, fixedRate, timeUnit);
            return this;
        }

        public <T> ProducerBuilder<T> receive(Class<T> expectedData)
        {
            var flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());

            repeatScheduler.register(flowGraph);

            jLegMed.addFlowGraph(flowGraph, repeatScheduler);

            return new ProducerBuilder<>(flowGraph, expectedData);
        }
    }

}
