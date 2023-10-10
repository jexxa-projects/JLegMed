package io.jexxa.jlegmed.core;

import java.util.Properties;

public abstract class ProducerURL {
    private FlowGraph flowGraph;
    private JLegMed jLegMed;
    private Properties properties;

    abstract Producer getProducer();

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

    public JLegMed and() {
        return jLegMed;
    }

    void setProperties(Properties properties)
    {
        this.properties = properties;
    }
}
