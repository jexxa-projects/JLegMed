package io.jexxa.jlegmed.core.flowgraph;

public interface Processor {
    Content process(Content content, Context context);
}