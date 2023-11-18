package io.jexxa.jlegmed.core.flowgraph;

public interface FlowGraphScheduler {
    void start();
    void stop();

    void schedule(Runnable passiveProducer);
}
