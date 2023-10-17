package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.ProducerURL;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

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

    public FlowGraph untilStopped() {
        getProducer().untilStopped();
        return getFlowGraph();
    }

    public FlowGraph onlyOnce() {
        getProducer().onlyOnce();
        return getFlowGraph();
    }

    public static InputStreamURL inputStreamOf(InputStream inputStream) {
        return new InputStreamURL(inputStream);
    }
}