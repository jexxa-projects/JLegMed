package io.jexxa.jlegmed;


import io.jexxa.jlegmed.jexxacp.scheduler.IScheduled;
import io.jexxa.jlegmed.jexxacp.scheduler.Scheduler;
import io.jexxa.jlegmed.processor.Processor;
import io.jexxa.jlegmed.producer.Producer;

import java.util.concurrent.TimeUnit;

public final class EachFlowGraph implements IScheduled, FlowGraph
{
    private final Scheduler scheduler = new Scheduler();
    private Class<?> expectedData;
    private Producer producer;
    private Processor processor;
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

    public <T extends Processor> EachFlowGraph andProcessWith(Class<T> clazz)
    {
        try {
            this.processor = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }

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
        processor.process( producer.receive(expectedData) );
    }


}
