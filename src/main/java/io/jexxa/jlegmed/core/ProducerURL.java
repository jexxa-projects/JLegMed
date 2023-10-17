package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.Producer;

public abstract class ProducerURL {
    private FlowGraph flowGraph;

    protected abstract Producer getProducer();

    void setFlowGraph(FlowGraph flowGraph) {
        this.flowGraph = flowGraph;
    }

    protected FlowGraph getFlowGraph()
    {
        return flowGraph;
    }
}
