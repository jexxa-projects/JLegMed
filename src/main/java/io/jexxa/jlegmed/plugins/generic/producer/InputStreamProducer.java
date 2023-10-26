package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamProducer<T> extends FunctionalProducer<T> {

    private final InputStream inputStream;
    private final Gson gson = new Gson();

    public enum ProducerMode{ONLY_ONCE, UNTIL_STOPPED}

    private InputStreamProducer(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }

    @Override
    protected T doProduce() {
        ProducerMode producerMode = getFilterConfig(ProducerMode.class).orElse(ProducerMode.ONLY_ONCE);
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