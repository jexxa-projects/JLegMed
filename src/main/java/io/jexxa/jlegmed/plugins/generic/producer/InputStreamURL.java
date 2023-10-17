package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;
import io.jexxa.jlegmed.core.producer.Producer;
import io.jexxa.jlegmed.core.producer.ProducerURL;

import java.io.InputStream;

public class InputStreamURL implements ProducerURL {

    private FlowGraph flowGraph;
    private final InputStream inputStream;
    private InputStreamProducer inputStreamProducer;

    public InputStreamURL(InputStream inputStreamReader)
    {
        this.inputStream = inputStreamReader;
    }

    public Producer init(ScheduledFlowGraph flowGraph)
    {
        this.flowGraph = flowGraph;
        if (inputStreamProducer == null )
        {
            this.inputStreamProducer = new InputStreamProducer(inputStream);
        }
        return inputStreamProducer;
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