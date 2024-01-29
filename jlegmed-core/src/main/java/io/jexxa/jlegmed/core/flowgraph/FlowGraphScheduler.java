package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.common.drivingadapter.scheduler.ScheduledFixedRate;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;

import java.util.concurrent.TimeUnit;

public class FlowGraphScheduler  {

    private Scheduler scheduler;
    private PassiveProducer<?> passiveProducer;
    private long maxIterations = 0;
    private long currentIterations = 0;

    public void start()
    {
        if (scheduler != null) {
            scheduler.start();
        }
    }

    public void stop()
    {
        if (scheduler != null) {
            scheduler.stop();
        }
    }

    public synchronized void waitUntilFinished()
    {
        try {
            while (currentIterations < maxIterations) {
                this.wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public <T> void configureFixedRate(PassiveProducer<T> producer, long period, TimeUnit timeUnit)
    {
        this.scheduler = new Scheduler();
        scheduler.register(new ScheduledFixedRate(producer::produceData, 0, period,timeUnit));
    }

    public <T> void configureRepeatedRate(PassiveProducer<T> producer, long repeat, long period, TimeUnit timeUnit)
    {
        this.scheduler = new Scheduler();
        this.passiveProducer = producer;
        this.maxIterations = repeat;
        this.currentIterations = 0;
        scheduler.register(new ScheduledFixedRate(this::countedProduceData, 0, period,timeUnit));
    }


    private synchronized void countedProduceData() {
        if (currentIterations < maxIterations) {
            ++currentIterations;
            passiveProducer.produceData();
        } else {
            stop();
        }
        this.notifyAll();
    }

}
