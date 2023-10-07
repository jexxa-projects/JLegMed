package io.jexxa.jlegmed;


import io.jexxa.jlegmed.jexxacp.scheduler.IScheduled;
import io.jexxa.jlegmed.jexxacp.scheduler.Scheduler;
import io.jexxa.jlegmed.producer.Producer;

import java.util.concurrent.TimeUnit;

public final class ScheduledFlowGraph extends AbstractFlowGraph implements IScheduled {
    private final Scheduler scheduler = new Scheduler();
    private final int fixedRate;
    private final TimeUnit timeUnit;
    private Producer producer;
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
        processMessage( new Message(producer.produce(expectedData)));
    }

}
