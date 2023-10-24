package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.Properties;

public class FlowGraph {
    private final Context context;

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

    private Producer<?> producer;

    public void start() {
        producer.start();
    }

    public void stop() {
        producer.stop();
    }

    public void setProducer(Producer<?> producer)
    {
        this.producer = producer;
        producer.setContext(getContext());
    }


}
