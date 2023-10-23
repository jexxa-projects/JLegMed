package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.producer.ActiveProducerURL;

public class SourceConnector<T> {

    private final Class<T> sourceType;
    private final ActiveFlowGraph activeFlowGraph;


    public SourceConnector(Class<T> sourceType, ActiveFlowGraph activeFlowGraph)
    {
        this.sourceType = sourceType;
        this.activeFlowGraph = activeFlowGraph;
    }

    public <U extends ActiveProducerURL<T>> U from(U producerURL) {
        try {
            var producer = producerURL.init(this);
            activeFlowGraph.setActiveProducer(producer);

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return producerURL;
    }

    public Class<T> getSourceType()
    {
        return sourceType;
    }

    public Context getContext()
    {
        return activeFlowGraph.getContext();
    }
}
