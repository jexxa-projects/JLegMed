package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.common.drivingadapter.scheduler.ScheduledFixedRate;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;

import java.util.concurrent.TimeUnit;

public class FlowGraphScheduler  {

    public record FixedRate(long period, TimeUnit timeUnit){}
    public record RepeatedRate(  long maxIteration, FixedRate fixedRate){}

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


    public <T> void configureFixedRate(PassiveProducer<T> producer, FixedRate fixedRate)
    {
        this.scheduler = new Scheduler();
        scheduler.register(new ScheduledFixedRate(producer::produceData, 0, fixedRate.period(),fixedRate.timeUnit()));
    }

    public <T> void configureRepeatedRate(PassiveProducer<T> producer, RepeatedRate repeatedRate)
    {
        this.scheduler = new Scheduler();
        this.passiveProducer = producer;
        this.maxIterations = repeatedRate.maxIteration();
        this.currentIterations = 0;
        scheduler.register(new ScheduledFixedRate(this::countedProduceData, 0, repeatedRate.fixedRate().period(), repeatedRate.fixedRate().timeUnit()));
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
