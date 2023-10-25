package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.processor.Processor;

public class InputPipe<T> {

    private final Processor<T, ?> processor;

    public InputPipe(Processor<T, ?> processor)
    {
        this.processor = processor;
    }
    public void receive(T content, Context context) {
        processor.process(content, context);
    }

}
