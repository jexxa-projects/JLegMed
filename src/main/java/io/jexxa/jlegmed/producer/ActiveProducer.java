package io.jexxa.jlegmed.producer;

import io.jexxa.jlegmed.FlowGraph;

import java.util.Properties;

public interface ActiveProducer {
    void init(Properties properties, FlowGraph flowGraph);
    void start();
    void stop();
}
