package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.flowgraph.ActiveProducer;

public class ActiveFlowGraph<T> extends AbstractFlowGraph {

    private ActiveProducer activeProducer;
    private final Class<T> inputDataType;
    public ActiveFlowGraph(JLegMed jLegMed, Class<T> inputDataType)
    {
        super(jLegMed);
        this.inputDataType = inputDataType;
    }

    public <U extends ActiveProducer> JLegMed generatedWith(Class<U> clazz) {
        try {
            this.activeProducer = clazz.getDeclaredConstructor().newInstance();
            activeProducer.init(getContext().getProperties(), this);

        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return getjLegMed();
    }

    public <U extends ActiveProducerURL> U from(U producerURL) {
        try {
            producerURL.setApplication(getjLegMed());
            producerURL.init(this);
            this.activeProducer = producerURL.getActiveProducer();

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
