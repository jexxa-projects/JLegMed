package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

public class AwaitBuilder<T> {
    private final Class<T> sourceType;
    private final FlowGraph flowGraph;

    AwaitBuilder(FlowGraph flowGraph, Class<T> sourceType) {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
    }

    public Binding<T> from(ActiveProducer<T> producer) {
        producer.producingType(sourceType);
        flowGraph.setProducer(producer);

        return new Binding<>(producer, producer.outputPipe(), flowGraph);
    }
}
