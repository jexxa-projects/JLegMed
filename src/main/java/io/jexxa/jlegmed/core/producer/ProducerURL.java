package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

public abstract class ProducerURL {
    private FlowGraph flowGraph;

    public abstract Producer getProducer();

    public void setFlowGraph(FlowGraph flowGraph) {
        this.flowGraph = flowGraph;
    }

    protected FlowGraph getFlowGraph()
    {
        return flowGraph;
    }
}
