package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

public interface ActiveProducerURL<T> {
    ActiveProducer<T> init(FlowGraph<T> flowGraph);
}
