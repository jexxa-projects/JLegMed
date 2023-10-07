package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.scheduler.IScheduled;
import io.jexxa.jlegmed.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

public final class ScheduledFlowGraph extends AbstractFlowGraph implements IScheduled {
    private final Scheduler scheduler = new Scheduler();
    private final int fixedRate;
    private final TimeUnit timeUnit;
    private Producer producer;
    private ContextProducer contextProducer;
    private Class<?> expectedData;

    public <T> ScheduledFlowGraph receive(Class<T> expectedData)
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

    public JLegMed from(ContextProducer contextProducer) {
        try {
            this.contextProducer = contextProducer;
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return getjLegMed();
    }

    public ScheduledFlowGraph(JLegMed jLegMed, int fixedRate, TimeUnit timeUnit)
    {
        super(jLegMed);
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
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
        if (contextProducer != null)
        {
            processMessage(new Message(contextProducer.produce(expectedData, getContext())));
        } else {
            processMessage( new Message(producer.produce(expectedData)));
        }
    }

}
