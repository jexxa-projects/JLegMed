package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.common.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

public class TypedOutputPipe<T> implements OutputPipe
{
    private TypedInputPipe<?> inputPipe;

    @Override
    public void forward(Content content, Context context) {
        if (inputPipe != null)
        {
            inputPipe.receive(content, context);
        } else {
            SLF4jLogger.getLogger(TypedOutputPipe.class).debug("No input pipe connected");
        }
    }

    public void connectTo(TypedInputPipe<?> inputPipe)
    {
        this.inputPipe = inputPipe;
    }

}
