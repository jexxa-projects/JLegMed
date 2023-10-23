package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.TypedConnector;
import io.jexxa.jlegmed.core.processor.TypedOutputPipe;

public abstract class ProducerURL<T> {

    public abstract void init(TypedProducer<T> producer);

    protected TypedConnector<T> getConnector()
    {
        return new TypedConnector<>(getOutputPipe(), null);
    }

    protected abstract TypedOutputPipe<T> getOutputPipe();

}
