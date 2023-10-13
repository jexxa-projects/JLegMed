package io.jexxa.jlegmed.core.flowgraph;

import java.util.Properties;

public interface ActiveProducer {
    void init(Properties properties, FlowGraph flowGraph);
    void start();
    void stop();
}
