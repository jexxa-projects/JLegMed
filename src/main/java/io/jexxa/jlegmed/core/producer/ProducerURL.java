package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.TypedConnector;
import io.jexxa.jlegmed.core.processor.OutputPipe;

public abstract class ProducerURL<T> {
    private TypedProducer<T> typedProducer;

    public void init(TypedProducer<T> producer)
    {
        this.typedProducer = producer;
        doInit(producer);
    }

    protected TypedConnector<T> getConnector()
    {
        return new TypedConnector<>(getOutputPipe(), null);
    }

    protected OutputPipe<T> getOutputPipe()
    {
        if (typedProducer != null) {
            return typedProducer.getOutputPipe();
        }

        throw new IllegalStateException("Method Init if producer URL was not called");
    }

    protected abstract void doInit(TypedProducer<T> producer);

}
