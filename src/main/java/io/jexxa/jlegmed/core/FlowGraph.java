package io.jexxa.jlegmed.core;

public interface FlowGraph {
    <T extends Processor> void andProcessWith(Class<T> clazz);
    void andProcessWith(Processor processor);
    void andProcessWith(ContextProcessor processor);

    void start();
    void stop();

    void processMessage(Message message);
}
