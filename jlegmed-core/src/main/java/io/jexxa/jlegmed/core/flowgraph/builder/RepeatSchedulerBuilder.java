package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class RepeatSchedulerBuilder {
    private final int times;
    private final FlowGraph flowGraph;

    private long fixedRate = 1;
    private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

    public RepeatSchedulerBuilder(int times, FlowGraph flowGraph) {
        this.times = times;
        this.flowGraph = flowGraph;
    }

    public RepeatSchedulerBuilder atInterval(long fixedRate, TimeUnit timeUnit) {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        return this;
    }

    public <T> PassiveProducerBuilder<T> receive(Class<T> expectedData) {
        return new PassiveProducerBuilder<>(flowGraph, expectedData, fixedRate, timeUnit, times);
    }
}
