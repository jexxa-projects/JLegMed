package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;

public interface ProducerURL {

    Producer init(ScheduledFlowGraph flowGraph);
}
