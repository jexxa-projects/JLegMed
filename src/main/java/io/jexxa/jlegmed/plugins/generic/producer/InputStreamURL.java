package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.ProducerURL;

import java.io.InputStream;

public class InputStreamURL extends ProducerURL {

    private final InputStream inputStream;
    private InputStreamProducer inputStreamProducer;

    public InputStreamURL(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }

    @Override
    public InputStreamProducer getProducer() {
        if (inputStreamProducer == null )
        {
            this.inputStreamProducer = new InputStreamProducer(inputStream);
        }
        return inputStreamProducer;
    }

    public JLegMed untilStopped() {
        getProducer().untilStopped();
        return getApplication();
    }

    public JLegMed onlyOnce() {
        getProducer().onlyOnce();
        return getApplication();
    }

    public static InputStreamURL inputStreamOf(InputStream inputStream) {
        return new InputStreamURL(inputStream);
    }
}