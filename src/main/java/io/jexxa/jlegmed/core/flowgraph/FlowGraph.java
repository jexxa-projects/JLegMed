package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.Properties;

public class FlowGraph<T> {
    private final Context context;

    private Producer<T> producer;
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

    public void setProducer(Producer<T> producer)
    {
        this.producer = producer;
        producer.setContext(getContext());
    }

    protected Producer<T> getProducer()
    {
        return producer;
    }


}
