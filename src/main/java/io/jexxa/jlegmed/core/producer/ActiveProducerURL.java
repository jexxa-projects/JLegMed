package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.ActiveFlowGraph;

public interface ActiveProducerURL {
    <T> ActiveProducer init(ActiveFlowGraph<T> flowGraph);
}
