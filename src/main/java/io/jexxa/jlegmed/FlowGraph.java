package io.jexxa.jlegmed;

import io.jexxa.jlegmed.processor.Processor;

public interface FlowGraph {
    <T extends Processor> void andProcessWith(Class<T> clazz);
    void andProcessWith(Processor processor);

    void start();
    void stop();
}
