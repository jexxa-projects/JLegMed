package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JSONReader<T> extends FunctionalProducer<T> {

    private final InputStream inputStream;
    private final Gson gson = new Gson();

    public enum ProducerMode{ONLY_ONCE, UNTIL_STOPPED}
    private final ProducerMode producerMode;
    private JSONReader(InputStream inputStreamReader, ProducerMode producerMode)
    {
        super(JSONReader.class.getSimpleName(), JSONReader.class);
        this.inputStream = inputStreamReader;
        this.producerMode = producerMode;
    }


    @Override
    protected T doProduce() {
        var result = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), producingType());

        try {
            if (producerMode == ProducerMode.UNTIL_STOPPED) {
                inputStream.reset();
            }
        } catch (IOException _)
        {
            return null;
        }

        return result;
    }

    public static <T> JSONReader<T> jsonStream(InputStream inputStream, ProducerMode producerMode) {
        return new JSONReader<>(inputStream, producerMode);
    }

    public static <T> JSONReader<T> jsonStreamUntilStopped(InputStream inputStream) {
        return jsonStream(inputStream, ProducerMode.UNTIL_STOPPED);
    }

    public static <T> JSONReader<T> jsonStreamOnlyOnce(InputStream inputStream) {
        return new JSONReader<>(inputStream, ProducerMode.ONLY_ONCE);
    }

}