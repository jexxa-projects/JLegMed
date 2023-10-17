package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.Properties;

public interface ActiveProducer {
    void init(Properties properties, FlowGraph flowGraph);
    void start();
    void stop();
}
