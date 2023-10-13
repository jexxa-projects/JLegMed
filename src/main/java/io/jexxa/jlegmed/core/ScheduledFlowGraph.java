package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Producer;
import io.jexxa.jlegmed.core.scheduler.IScheduled;
import io.jexxa.jlegmed.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

public final class ScheduledFlowGraph extends AbstractFlowGraph implements IScheduled {
    private final Scheduler scheduler = new Scheduler();
    private final int fixedRate;
    private final TimeUnit timeUnit;
    private Producer producer;
    private Class<?> expectedData;



    public <T> TypedProducer<T> receive(Class<T> expectedData)
    {
        this.expectedData = expectedData;
        return new TypedProducer<>(this);
    }

    public <T extends ProducerURL> T from(T producerURL) {
        producerURL.setApplication(getjLegMed());
        this.producer = producerURL.getProducer();

        return producerURL;
    }

    public JLegMed generatedWith(Producer producer) {
        try {
            this.producer = producer;
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
        var result = producer.produce(expectedData, getContext());

        if ( result != null) {
            processMessage(new Content(result));
        }
    }

}
