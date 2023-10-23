package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.common.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.flowgraph.Context;

public class TypedOutputPipe<T> implements OutputPipe<T>
{
    private InputPipe<T> inputPipe;

    @Override
    public void forward(T content, Context context) {
        if (inputPipe != null)
        {
            inputPipe.receive(content, context);
        } else {
            SLF4jLogger.getLogger(TypedOutputPipe.class).debug("No input pipe connected");
        }
    }

    @Override
    public void connectTo(InputPipe<T> inputPipe)
    {
        this.inputPipe = inputPipe;
    }

}
