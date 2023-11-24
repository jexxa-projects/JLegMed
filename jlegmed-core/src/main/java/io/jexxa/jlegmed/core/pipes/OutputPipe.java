package io.jexxa.jlegmed.core.pipes;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.commons.facade.logger.SLF4jLogger.getLogger;

public class OutputPipe<T> {
    private InputPipe<T> inputPipe;

    public void connectTo(InputPipe<T> inputPipe)
    {
        this.inputPipe = inputPipe;
    }

    public void forward(T data) {
        getInvocationHandler(this).invoke(this, this::forwardToSuccessor, data);
    }

    private void forwardToSuccessor(T data)
    {
        if (data == null) {
            return;
        }

        if (inputPipe == null)
        {
            getLogger(OutputPipe.class).debug("No input pipe connected");
            return;
        }

        inputPipe.receive(data);
    }
}
