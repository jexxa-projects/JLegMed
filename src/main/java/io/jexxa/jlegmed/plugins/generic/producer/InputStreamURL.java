package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.core.producer.TypedProducer;

import java.io.InputStream;

public class InputStreamURL implements ProducerURL {

    private FlowGraph flowGraph;
    private final InputStream inputStream;
    private InputStreamProducer inputStreamProducer;

    public InputStreamURL(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }

    public <T> void init(TypedProducer<T> typedProducer)
    {
        if (inputStreamProducer == null )
        {
            this.inputStreamProducer = new InputStreamProducer(inputStream);
            flowGraph = typedProducer.getFlowGraph();
        }
        typedProducer.generatedWith(inputStreamProducer::produce);
    }

    public FlowGraph untilStopped() {
        inputStreamProducer.untilStopped();
        return flowGraph;
    }

    public FlowGraph onlyOnce() {
        inputStreamProducer.onlyOnce();
        return flowGraph;
    }

    public static InputStreamURL inputStreamOf(InputStream inputStream) {
        return new InputStreamURL(inputStream);
    }
}