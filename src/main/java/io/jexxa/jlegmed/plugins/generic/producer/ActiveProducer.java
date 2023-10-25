package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.common.scheduler.IScheduled;
import io.jexxa.jlegmed.common.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.Binding;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class ActiveProducer<T> extends TypedProducer<T> implements IScheduled {

    private int fixedRate = 5;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private final Scheduler scheduler = new Scheduler();
    @Override
    public void start() {
        scheduler.register(this);
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
        produceData(getContext());
    }


    public ActiveProducer<T> using(Function<Context, T> function)
    {
        with(function);
        return this;
    }

    @SuppressWarnings("unused")
    public ActiveProducer<T> using(Supplier<T> function)
    {
        with(function);
        return this;
    }

    public Binding<T> withInterval(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        return getConnector();
    }

    public static <T> ActiveProducer<T> activeProducer() {
        return new ActiveProducer<>();
    }

}
