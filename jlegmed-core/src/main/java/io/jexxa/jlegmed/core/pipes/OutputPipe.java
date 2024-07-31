package io.jexxa.jlegmed.core.pipes;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;

public class OutputPipe<T> {
    private InputPipe<T> inputPipe;

    public void connectTo(InputPipe<T> inputPipe)
    {
        this.inputPipe = inputPipe;
    }

    public void forward(T data) {
        getInvocationHandler(this).invoke(this, this::forwardToSuccessor, data);
    }

    public boolean isConnected()
    {
        return inputPipe != null;
    }

    private void forwardToSuccessor(T data)
    {
        if (data == null) {
            return;
        }

        if (!isConnected())
        {
            getLogger(OutputPipe.class).debug("No input pipe connected");
            return;
        }

        inputPipe.receive(data);
    }

    protected InputPipe<T> inputPipe() {
        return inputPipe;
    }
}
