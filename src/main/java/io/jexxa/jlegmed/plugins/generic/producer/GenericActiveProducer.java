package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.ActiveProducer;
import io.jexxa.jlegmed.core.FlowGraph;
import io.jexxa.jlegmed.core.Content;
import io.jexxa.jlegmed.core.scheduler.IScheduled;
import io.jexxa.jlegmed.core.scheduler.Scheduler;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class GenericActiveProducer implements ActiveProducer, IScheduled {
    private static final int FIXED_RATE = 5;
    private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private final Scheduler scheduler = new Scheduler();

    private FlowGraph flowGraph;
    private int counter = 0;

    @Override
    public void init(Properties properties, FlowGraph flowGraph) {
        scheduler.register(this);
        this.flowGraph = flowGraph;
    }

    @Override
    public void start() {
        scheduler.start();
    }

    @Override
    public void stop() {
        scheduler.stop();
    }

    @Override
    public int fixedRate() {
        return FIXED_RATE;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public void execute()
    {
        ++counter;
        flowGraph.processMessage(new Content(counter));
    }
}
