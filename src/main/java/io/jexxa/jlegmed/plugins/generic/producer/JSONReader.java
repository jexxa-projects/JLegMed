package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONReader<T> extends FunctionalProducer<T> {

    private final InputStream inputStream;
    private final Gson gson = new Gson();

    public enum ProducerMode{ONLY_ONCE, UNTIL_STOPPED}
    private ProducerMode producerMode;
    private JSONReader(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }

    @Override
    public void init()
    {
        super.init();
        producerMode = filterContext().getFilterConfig(ProducerMode.class).orElse(ProducerMode.ONLY_ONCE);
    }

    @Override
    protected T doProduce() {
        var result = gson.fromJson(new InputStreamReader(inputStream), producingType());

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

    public static <T> JSONReader<T> inputStream(InputStream inputStream) {
        return new JSONReader<>(inputStream);
    }
}