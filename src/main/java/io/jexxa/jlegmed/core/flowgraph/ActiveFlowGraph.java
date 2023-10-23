package io.jexxa.jlegmed.core.flowgraph;


import io.jexxa.jlegmed.core.producer.ActiveProducer;

import java.util.Properties;

public class ActiveFlowGraph extends FlowGraph {

    private ActiveProducer<?> activeProducer;
    public ActiveFlowGraph(String flowGraphID, Properties properties)
    {
        super(flowGraphID,  properties );
    }

    @Override
    public void start() {
        activeProducer.start();
    }

    @Override
    public void stop() {
        activeProducer.stop();
    }

    public void setActiveProducer(ActiveProducer<?> activeProducer)
    {
        this.activeProducer = activeProducer;
    }
}
