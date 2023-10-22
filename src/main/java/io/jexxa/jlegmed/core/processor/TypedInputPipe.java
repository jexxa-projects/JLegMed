package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

public class TypedInputPipe<T> implements InputPipe {

    private final TypedProcessor<T, ?> processor;

    public TypedInputPipe(TypedProcessor<T, ?> processor)
    {
        this.processor = processor;
    }
    @Override
    public void receive(Content content, Context context) {
        processor.process(content, context);
    }
}
