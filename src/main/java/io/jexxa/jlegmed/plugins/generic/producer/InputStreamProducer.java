package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamProducer<T> extends TypedProducer<T> {

    private final InputStream inputStream;
    private final Gson gson = new Gson();
    private ProducerMode producerMode = ProducerMode.ONLY_ONCE;

    public enum ProducerMode{ONLY_ONCE, UNTIL_STOPPED}

    private InputStreamProducer(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }


    @Override
    protected void doInit()
    {
        producerMode = getFilterConfig(ProducerMode.class).orElse(ProducerMode.ONLY_ONCE);
        with(this::produce);
    }

    public T produce() {
        var result = gson.fromJson(new InputStreamReader(inputStream), getType());

        try {
            if (producerMode == ProducerMode.UNTIL_STOPPED) {
                inputStream.reset();
            }
        } catch (IOException e)
        {
            return null;
        }

        return result;
    }

    public static <T> InputStreamProducer<T> inputStream(InputStream inputStream) {
        return new InputStreamProducer<>(inputStream);
    }
}