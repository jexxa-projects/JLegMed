package io.jexxa.jlegmed;

import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.producer.Producer;
import io.jexxa.jlegmed.producer.URL;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFlowGraph implements FlowGraph {
    private Class<?> expectedData;
    private final List<Processor> processorList = new ArrayList<>();
    private Producer producer;

    private final JLegMed jLegMed;

    AbstractFlowGraph(JLegMed jLegMed)
    {
        this.jLegMed = jLegMed;
    }
    public <T> AbstractFlowGraph receive(Class<T> expectedData)
    {
        this.expectedData = expectedData;
        return this;
    }
    public <T extends Producer> JLegMed from(Class<T> clazz) {
        try {
            this.producer = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return getjLegMed();
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



    public <T extends Processor> void andProcessWith(Class<T> clazz)
    {
        try {
            this.processorList.add(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }

    }

    @Override
    public void andProcessWith(Processor processor) {
        processorList.add(processor);
    }

    public void processMessage(Message message)
    {
        var result = message;

        for (Processor processor : processorList) {
            result = processor.process(result);
            if (result == null)
            {
                return;
            }
        }
    }

    protected JLegMed getjLegMed()
    {
        return jLegMed;
    }

    protected Class<?> getExpectedData() { return expectedData; }

    protected Producer getProducer()
    {
        return producer;
    }

}
