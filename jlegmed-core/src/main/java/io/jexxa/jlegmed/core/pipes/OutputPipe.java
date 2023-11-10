package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class OutputPipe<T> {
    private InputPipe<T> inputPipe;
    private final List<BiConsumer<OutputPipe<?>, Object>> beforeInterceptors = new ArrayList<>();

    public void connectTo(InputPipe<T> inputPipe)
    {
        this.inputPipe = inputPipe;
    }

    public void forward(T data) {
        beforeInterceptors.forEach( beforeInterceptor -> beforeInterceptor.accept(this, data));
        forwardToSuccessor(data);
    }

    public void interceptBefore(BiConsumer<OutputPipe<?>, Object> beforeInterceptor) {
        beforeInterceptors.add(beforeInterceptor);
    }

    private void forwardToSuccessor(T data)
    {
        if (inputPipe != null)
        {
            if (data != null) {
                inputPipe.receive(data);
            }
        } else {
            SLF4jLogger.getLogger(OutputPipe.class).debug("No input pipe connected");
        }
    }

}
