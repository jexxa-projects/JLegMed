package io.jexxa.jlegmed.core.flowgraph;


import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.Properties;

public class ActiveFlowGraph extends FlowGraph {

    private Producer<?> producer;
    public ActiveFlowGraph(String flowGraphID, Properties properties)
    {
        super(flowGraphID,  properties );
    }

    @Override
    public void start() {
        producer.start();
    }

    @Override
    public void stop() {
        producer.stop();
    }

    public void setActiveProducer(Producer<?> producer)
    {
        this.producer = producer;
    }
}
