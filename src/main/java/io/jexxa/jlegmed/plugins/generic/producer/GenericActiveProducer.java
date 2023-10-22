package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.common.scheduler.IScheduled;
import io.jexxa.jlegmed.common.scheduler.Scheduler;
import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.producer.ActiveProducer;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericActiveProducer implements ActiveProducer, IScheduled {
    private int fixedRate = 5;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private final Scheduler scheduler = new Scheduler();

    private Function<Context, Object> contextFunction;
    private Supplier<Object> supplier;

    private final FlowGraph flowGraph;

    public GenericActiveProducer(FlowGraph flowGraph)
    {
        this.flowGraph = flowGraph;
        scheduler.register(this);
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
        return fixedRate;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public void execute()
    {
        Object result = null;
        if (contextFunction != null) {
            result = contextFunction.apply(flowGraph.getContext());
        }
        if (supplier != null) {
            result = supplier.get();
        }
        if (result != null )
        {
            flowGraph.processMessage(new Content(result));
        }
    }

    public void setInterval(int fixedRate, TimeUnit timeUnit) {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    public void setFunction(Function<Context, Object> function) {
        this.contextFunction = function;
    }

    public void setSupplier(Supplier<Object> supplier) {
        this.supplier = supplier;
    }
}
