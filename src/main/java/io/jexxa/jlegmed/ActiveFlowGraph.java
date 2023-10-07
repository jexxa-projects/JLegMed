package io.jexxa.jlegmed;


import io.jexxa.jlegmed.producer.ActiveProducer;

import java.util.Properties;

public class ActiveFlowGraph extends AbstractFlowGraph {

    private ActiveProducer activeProducer;
    public ActiveFlowGraph(JLegMed jLegMed)
    {
        super(jLegMed);
    }

    public <T extends ActiveProducer> JLegMed from(Class<T> clazz) {
        try {
            this.activeProducer = clazz.getDeclaredConstructor().newInstance();
            activeProducer.init(new Properties(), this);

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return getjLegMed();
    }
    @Override
    public void start() {
        activeProducer.start();
    }

    @Override
    public void stop() {
        activeProducer.stop();
    }
}
