package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.flowgraph.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamProducer {
    private final InputStream inputStream;
    private final Gson gson = new Gson();
    private ProducerMode producerMode = ProducerMode.ONLY_ONCE;

    private enum ProducerMode{ONLY_ONCE, UNTIL_STOPPED}

    public InputStreamProducer(InputStream inputStream)
    {
        this.inputStream = inputStream;
        inputStream.mark(8000);
    }
    public <T> T produce(Context context, Class<T> clazz) {
        var result = gson.fromJson(new InputStreamReader(inputStream), clazz);

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

    public void untilStopped() {
        producerMode = ProducerMode.UNTIL_STOPPED;
    }

    public void onlyOnce() {
        producerMode = ProducerMode.ONLY_ONCE;
    }
}
