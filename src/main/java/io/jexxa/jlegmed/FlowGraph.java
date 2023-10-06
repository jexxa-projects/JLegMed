package io.jexxa.jlegmed;

import io.jexxa.jlegmed.processor.Processor;

public interface FlowGraph {
    public <T extends Processor> FlowGraph andProcessWith(Class<T> clazz);
    public FlowGraph andProcessWith(Processor processor);

}
