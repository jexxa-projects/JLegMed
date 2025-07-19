package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.core.filter.processor.Processor;

public class InputPipe<T> implements IInputPipe<T> {
    private final Processor<T, ?> processor;
    public InputPipe(Processor<T, ?> processor)
    {
        this.processor = processor;
    }
    @Override
    public void receive(T data) {
        processor.process(data);
    }
}
