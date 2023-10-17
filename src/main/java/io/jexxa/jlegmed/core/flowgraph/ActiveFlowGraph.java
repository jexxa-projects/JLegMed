package io.jexxa.jlegmed.core.flowgraph;


import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.producer.ActiveProducerURL;

import java.util.Properties;

public class ActiveFlowGraph<T> extends AbstractFlowGraph {

    private ActiveProducer activeProducer;
    private final Class<T> inputDataType;
    public ActiveFlowGraph(String flowGraphID, Properties properties, Class<T> inputDataType)
    {
        super(flowGraphID, properties);
        this.inputDataType = inputDataType;
    }

    public <U extends ActiveProducerURL> U from(U producerURL) {
        try {
            this.activeProducer = producerURL.init(this);

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return producerURL;
    }
    @Override
    public void start() {
        activeProducer.start();
    }

    @Override
    public void stop() {
        activeProducer.stop();
    }

    public Class<T> getInputDataType()
    {
        return inputDataType;
    }
}
