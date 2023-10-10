package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.FlowGraph;
import io.jexxa.jlegmed.core.Producer;

import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamProducer implements Producer {
    private final InputStream inputStream;
    private final FlowGraph flowGraph;

    private boolean gsonDeserializer;

    public InputStreamProducer(InputStream inputStream, FlowGraph flowGraph)
    {
        this.inputStream = inputStream;
        this.flowGraph = flowGraph;
    }
    @Override
    public <T> T produce(Class<T> clazz) {
        var reader = new InputStreamReader(inputStream);
        var gson = new Gson();
        return gson.fromJson(reader, clazz);
    }

    public void setGsonDeserializer()
    {
        this.gsonDeserializer = true;
    }
}
