package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.AbstractFlowGraph;

public interface ProducerURL {

    <T> Producer init(AbstractFlowGraph<T> flowGraph);
}
