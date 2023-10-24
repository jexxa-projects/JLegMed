package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.processor.TypedProcessor;

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
