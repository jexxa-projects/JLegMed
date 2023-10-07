package io.jexxa.jlegmed;

import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.processor.ContextProcessor;

public interface FlowGraph {
    <T extends Processor> void andProcessWith(Class<T> clazz);
    void andProcessWith(Processor processor);
    void andProcessWith(ContextProcessor processor);

    void start();
    void stop();

    void processMessage(Message message);
}
