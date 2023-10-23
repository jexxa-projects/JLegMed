package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.AbstractFlowGraph;

public interface ActiveProducerURL<T> {
    ActiveProducer init(AbstractFlowGraph<T> flowGraph);
}
