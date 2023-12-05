package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;

public class EveryBuilder {
    private final FlowGraph flowGraph;
    private final long fixedRate;
    private final TimeUnit timeUnit;

    public EveryBuilder(FlowGraph flowGraph, long fixedRate, TimeUnit timeUnit) {
        this.flowGraph = flowGraph;
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    public <T> ProducerBuilder<T> receive(Class<T> expectedData) {
        return new ProducerBuilder<>(flowGraph, expectedData, fixedRate, timeUnit);
    }
}
