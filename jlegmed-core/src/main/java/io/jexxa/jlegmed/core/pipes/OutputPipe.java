package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.core.filter.Filter;

import java.math.BigInteger;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static java.math.BigInteger.valueOf;

public class OutputPipe<T> {
    private InputPipe<T> inputPipe;
    private BigInteger forwardedMessages = valueOf(0);
    private final Filter filter;

    public OutputPipe(Filter filter)
    {
        this.filter = filter;
    }

    public Filter filter()
    {
        return filter;
    }

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

    public BigInteger forwardedMessages() {
        return forwardedMessages;
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
        forwardedMessages = forwardedMessages.add(valueOf(1));
    }

    protected InputPipe<T> inputPipe() {
        return inputPipe;
    }
}
