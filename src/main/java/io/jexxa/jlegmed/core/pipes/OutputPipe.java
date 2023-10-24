package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.Context;

public class OutputPipe<T> {
    private InputPipe<T> inputPipe;

    public void forward(T content, Context context) {
        if (inputPipe != null)
        {
            inputPipe.receive(content, context);
        } else {
            SLF4jLogger.getLogger(OutputPipe.class).debug("No input pipe connected");
        }
    }

    public void connectTo(InputPipe<T> inputPipe)
    {
        this.inputPipe = inputPipe;
    }
}
