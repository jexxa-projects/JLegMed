package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.ActiveProducer;

public abstract class ActiveProducerURL {
    private JLegMed jLegMed;

    protected abstract ActiveProducer getActiveProducer();

    public <T> void init(ActiveFlowGraph<T> flowGraph)
    {
        getActiveProducer().init(flowGraph.getContext().getProperties(), flowGraph);
    }

    void setApplication(JLegMed jLegMed) {
        this.jLegMed = jLegMed;
    }

    protected JLegMed getApplication()
    {
        return jLegMed;
    }
}
