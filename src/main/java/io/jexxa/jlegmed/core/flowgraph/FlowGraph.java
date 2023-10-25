package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;

import java.util.Properties;

public class FlowGraph<T> {
    private final Context context;

    private TypedProducer<T> producer;
    private final String flowGraphID;

    public FlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.context = new Context(properties);
    }

    public String getFlowGraphID()
    {
        return flowGraphID;
    }

    public Context getContext()
    {
        return context;
    }

    public void start() {
        producer.start();
    }

    public void stop() {
        producer.stop();
    }

    public void setProducer(TypedProducer<T> producer)
    {
        this.producer = producer;
        producer.setContext(getContext());
    }

    protected TypedProducer<T> getProducer()
    {
        return producer;
    }


}
