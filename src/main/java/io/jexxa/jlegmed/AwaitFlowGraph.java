package io.jexxa.jlegmed;


import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.producer.Producer;
import io.jexxa.jlegmed.producer.URL;

public final class AwaitFlowGraph implements FlowGraph
{
    private Class<?> expectedData;
    private Producer producer;
    private Processor processor;
    private final JLegMed jLegMed;

    public AwaitFlowGraph(JLegMed jLegMed)
    {
        this.jLegMed = jLegMed;
    }

    public <T> void receive(Class<T> expectedData)
    {
        this.expectedData = expectedData;
    }
    public URL from(String url) {
        try {
            return new URL(url, expectedData, jLegMed);
            //this.producer = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        //return jLegMed;
    }
    public <T extends Producer> JLegMed from(Class<T> clazz) {
        try {
            this.producer = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return jLegMed;
    }

    public <T extends Processor> FlowGraph andProcessWith(Class<T> clazz)
    {
        try {
            this.processor = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return this;
    }

    @Override
    public FlowGraph andProcessWith(Processor processor) {
        return this;
    }


    public void start()
    {
        processor.process( producer.receive(expectedData) );
    }

    public void stop()
    {

    }

}
