package io.jexxa.jlegmed;


import io.jexxa.jlegmed.jexxacp.scheduler.IScheduled;
import io.jexxa.jlegmed.jexxacp.scheduler.Scheduler;
import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.producer.Producer;
import io.jexxa.jlegmed.producer.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class EachFlowGraph implements IScheduled, FlowGraph
{
    private final Scheduler scheduler = new Scheduler();
    private Class<?> expectedData;
    private Producer producer;
    private final List<Processor> processorList = new ArrayList<>();
    private final int fixedRate;
    private final TimeUnit timeUnit;
    private final JLegMed jLegMed;


    public EachFlowGraph(JLegMed jLegMed, int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        this.jLegMed = jLegMed;
    }

    public <T> EachFlowGraph receive(Class<T> expectedData)
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
        return jLegMed;
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



     public <T extends Processor> EachFlowGraph andProcessWith(Class<T> clazz)
    {
        try {
            this.processorList.add(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return this;
    }

    @Override
    public FlowGraph andProcessWith(Processor processor) {
        processorList.add(processor);
        return this;
    }

    public void start()
    {
        scheduler.register(this);
        scheduler.start();
    }

    public void stop()
    {
        scheduler.stop();
    }

    @Override
    public int fixedRate() {
        return fixedRate;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public void execute() {

        var result = new Message(producer.produce(expectedData));

        for (Processor processor : processorList) {
            result = processor.process(result);
            if (result == null)
            {
                return;
            }
        }
    }

}
