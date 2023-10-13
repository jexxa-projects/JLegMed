package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.Producer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.Properties;

public abstract class ProducerURL {
    private FlowGraph flowGraph;
    private JLegMed jLegMed;
    private Properties properties;

    protected abstract Producer getProducer();

    void setFlowGraph(FlowGraph flowGraph) {
        this.flowGraph = flowGraph;
    }
    void setApplication(JLegMed jLegMed) {
        this.jLegMed = jLegMed;
    }

    protected FlowGraph getFlowGraph()
    {
        return flowGraph;
    }

    protected JLegMed getApplication()
    {
        return jLegMed;
    }


    void setProperties(Properties properties)
    {
        this.properties = properties;
    }
}
