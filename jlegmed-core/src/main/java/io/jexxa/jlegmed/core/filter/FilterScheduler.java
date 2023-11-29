package io.jexxa.jlegmed.core.filter;

public interface FilterScheduler {
    void start();
    void stop();
    void schedule(Runnable passiveProducer);
}
