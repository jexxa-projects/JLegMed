package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Context;
import io.jexxa.jlegmed.Message;

public interface ContextProcessor {
    Message process(Message message, Context context);
}
