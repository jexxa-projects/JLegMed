package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.processor.OutputPipe;

public interface ActiveProducer<T> {
    void start();
    void stop();

    OutputPipe<T> getOutputPipe();
}
