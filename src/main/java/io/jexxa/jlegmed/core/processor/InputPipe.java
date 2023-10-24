package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Context;

public class InputPipe<T> {

    private final TypedProcessor<T, ?> processor;

    public InputPipe(TypedProcessor<T, ?> processor)
    {
        this.processor = processor;
    }
    public void receive(T content, Context context) {
        processor.process(content, context);
    }

}
