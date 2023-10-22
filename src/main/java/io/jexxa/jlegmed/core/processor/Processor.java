package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

public interface Processor {
    void process(Content content, Context context);
}
