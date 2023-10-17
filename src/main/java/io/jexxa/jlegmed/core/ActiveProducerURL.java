package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.ActiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

public abstract class ActiveProducerURL {
    private FlowGraph flowgraph;

    protected abstract ActiveProducer getActiveProducer();

    public <T> void init(ActiveFlowGraph<T> flowGraph)
    {
        getActiveProducer().init(flowGraph.getContext().getProperties(), flowGraph);
    }

    void setFlowGraph(FlowGraph flowgraph) {
        this.flowgraph = flowgraph;
    }

    protected FlowGraph getFlowgraph()
    {
        return flowgraph;
    }
}
