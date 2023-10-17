package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.ActiveFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

public abstract class ActiveProducerURL {
    private FlowGraph flowgraph;

    public abstract ActiveProducer getActiveProducer();

    public <T> void init(ActiveFlowGraph<T> flowGraph)
    {
        getActiveProducer().init(flowGraph.getContext().getProperties(), flowGraph);
    }

    public void setFlowGraph(FlowGraph flowgraph) {
        this.flowgraph = flowgraph;
    }

    protected FlowGraph getFlowgraph()
    {
        return flowgraph;
    }
}
