package io.jexxa.jlegmed.core.flowgraph;

public interface FlowGraph {

    void andProcessWith(Processor processor);

    void start();
    void stop();

    void processMessage(Content content);
}
