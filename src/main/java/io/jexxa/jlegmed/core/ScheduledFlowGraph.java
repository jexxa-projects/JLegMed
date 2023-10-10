package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.scheduler.IScheduled;
import io.jexxa.jlegmed.core.scheduler.Scheduler;

import java.util.Properties;
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

    public JLegMed generatedWith(Producer producer) {
        try {
            this.producer = producer;
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return getjLegMed();
    }

    public <T extends ProducerURL> T from(T producerURL) {
        producerURL.setFlowGraph(this);
        producerURL.setApplication(getjLegMed());
        producerURL.setProperties(new Properties());
        this.producer = producerURL.getProducer();

        return producerURL;
    }

    public JLegMed generatedWith(ContextProducer contextProducer) {
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
        Object result;
        if (contextProducer != null)
        {
            result = contextProducer.produce(expectedData, getContext());
        } else {
            result = producer.produce(expectedData);
        }

        if ( result != null) {
            processMessage(new Content(result));
        }
    }

}