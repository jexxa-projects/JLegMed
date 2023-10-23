package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.flowgraph.TypedConnector;
import io.jexxa.jlegmed.core.processor.TypedOutputPipe;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.core.producer.TypedProducer;

import java.io.InputStream;

public class InputStreamURL<T> extends ProducerURL<T> {

    private final InputStream inputStream;
    private InputStreamProducer inputStreamProducer;
    private TypedProducer<T> typedProducer;

    public InputStreamURL(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }

    public void init(TypedProducer<T> typedProducer)
    {
        this.typedProducer = typedProducer;
        if (inputStreamProducer == null )
        {
            this.inputStreamProducer = new InputStreamProducer(inputStream);
        }
        typedProducer.generatedWith(inputStreamProducer::produce);
    }

    @Override
    protected TypedOutputPipe<T> getOutputPipe() {
        return typedProducer.getOutputPipe();
    }

    public TypedConnector<T> untilStopped() {
        inputStreamProducer.untilStopped();
        return getConnector();
    }

    public TypedConnector<T> onlyOnce() {
        inputStreamProducer.onlyOnce();
        return getConnector();
    }

    public static <T> InputStreamURL<T> inputStreamOf(InputStream inputStream) {
        return new InputStreamURL<>(inputStream);
    }
}