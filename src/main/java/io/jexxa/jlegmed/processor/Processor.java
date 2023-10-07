package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Message;

public interface Processor {
    Message process(Message message);
}
