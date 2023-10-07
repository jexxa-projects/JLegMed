package io.jexxa.jlegmed;

import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.producer.URL;

public interface FlowGraph {
    <T extends Processor> FlowGraph andProcessWith(Class<T> clazz);
    FlowGraph andProcessWith(Processor processor);

}
