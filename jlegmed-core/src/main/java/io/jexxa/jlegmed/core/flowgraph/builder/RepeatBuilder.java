package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class RepeatBuilder {
    private final int times;
    private final FlowGraph flowGraph;

    private long fixedRate = 1;
    private TimeUnit timeUnit = TimeUnit.NANOSECONDS;

    public RepeatBuilder(int times, FlowGraph flowGraph) {
        this.times = times;
        this.flowGraph = flowGraph;
    }

    public RepeatBuilder atInterval(long fixedRate, TimeUnit timeUnit) {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        return this;
    }

    public <T> ProducerBuilder<T> receive(Class<T> expectedData) {
        return new ProducerBuilder<>(flowGraph, expectedData, fixedRate, timeUnit, times);
    }
}
