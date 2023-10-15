package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.flowgraph.ActiveProducer;

public class ActiveFlowGraph extends AbstractFlowGraph {

    private ActiveProducer activeProducer;
    public ActiveFlowGraph(JLegMed jLegMed)
    {
        super(jLegMed);
    }

    public <T extends ActiveProducer> JLegMed generatedWith(Class<T> clazz) {
        try {
            this.activeProducer = clazz.getDeclaredConstructor().newInstance();
            activeProducer.init(getContext().getProperties(), this);

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
