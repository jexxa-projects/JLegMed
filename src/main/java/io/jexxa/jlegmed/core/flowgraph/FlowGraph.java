package io.jexxa.jlegmed.core.flowgraph;

public interface FlowGraph {

    void start();
    void stop();

    Context getContext();
}
