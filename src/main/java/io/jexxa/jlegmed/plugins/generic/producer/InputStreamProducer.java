package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.Producer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamProducer implements Producer {
    private final InputStream inputStream;
    private final Gson gson = new Gson();

    public InputStreamProducer(InputStream inputStream)
    {
        this.inputStream = inputStream;
        inputStream.mark(8000);
    }
    @Override
    public <T> T produce(Class<T> clazz) {
        try {
            if (inputStream.available() == 0) {
                inputStream.reset();
            }
        } catch (IOException e)
        {
            return null;
        }

        return gson.fromJson(new InputStreamReader(inputStream), clazz);
    }
}
