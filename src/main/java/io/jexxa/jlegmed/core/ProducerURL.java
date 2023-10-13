package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.Producer;

public abstract class ProducerURL {
    private JLegMed jLegMed;

    protected abstract Producer getProducer();

    void setApplication(JLegMed jLegMed) {
        this.jLegMed = jLegMed;
    }

    protected JLegMed getApplication()
    {
        return jLegMed;
    }
}
