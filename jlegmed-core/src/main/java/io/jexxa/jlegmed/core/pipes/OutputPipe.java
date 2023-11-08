package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;

public class OutputPipe<T> {
    private InputPipe<T> inputPipe;

    public void connectTo(InputPipe<T> inputPipe)
    {
        this.inputPipe = inputPipe;
    }

    public void forward(T data) {
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
