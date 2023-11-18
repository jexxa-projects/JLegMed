package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.flowgraph.scheduler.FixedRateScheduler;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class FixedRateSchedulerBuilder {
    private final FlowGraph flowGraph;

    public FixedRateSchedulerBuilder(FlowGraph flowGraph, int fixedRate, TimeUnit timeUnit) {
        this.flowGraph = flowGraph;

        flowGraph.scheduler(new FixedRateScheduler(fixedRate, timeUnit));
    }

    public <T> PassiveProducerBuilder<T> receive(Class<T> expectedData) {
        return new PassiveProducerBuilder<>(flowGraph, expectedData);
    }
}
