package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

public interface Processor {
    Content process(Content content, Context context);
    <T> void setConfiguration(T configuration);

    Object getConfiguration();
}
