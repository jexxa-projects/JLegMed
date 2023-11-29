package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.filter.scheduler.RepeatScheduler;

import java.util.concurrent.TimeUnit;

public class RepeatSchedulerBuilder {
    private final int times;
    private RepeatScheduler repeatScheduler;
    private final FlowGraph flowGraph;

    public RepeatSchedulerBuilder(int times, FlowGraph flowGraph) {
        this.repeatScheduler = new RepeatScheduler(times);
        this.times = times;
        this.flowGraph = flowGraph;
    }

    public RepeatSchedulerBuilder atInterval(int fixedRate, TimeUnit timeUnit) {
        this.repeatScheduler = new RepeatScheduler(times, fixedRate, timeUnit);
        return this;
    }

    public <T> PassiveProducerBuilder<T> receive(Class<T> expectedData) {
        flowGraph.scheduler(repeatScheduler);

        return new PassiveProducerBuilder<>(flowGraph, expectedData);
    }
}
