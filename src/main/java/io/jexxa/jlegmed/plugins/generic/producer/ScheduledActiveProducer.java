package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.common.component.scheduler.IScheduled;
import io.jexxa.jlegmed.common.component.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ScheduledActiveProducer<T> extends Producer<T> implements IScheduled {
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
        getOutputPipe().forward(produceData(), getContext());
    }

    protected abstract T produceData();

    public ScheduledActiveProducer<T> withInterval(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        return this;
    }

    public record ScheduledActiveProducerConfig(int fixedRate, TimeUnit timeUnit)
    {

    }


    public static <T> ScheduledActiveProducer<T> activeProducer(BiFunction<Context, Class<T>, T> biFunction) {
        return new ScheduledActiveProducer<>() {
            @Override
            protected T produceData() {
                return biFunction.apply(getContext(), getType());
            }
        };
    }
    public static <T> ScheduledActiveProducer<T> activeProducer(Function<Context, T> contextFunction) {
        return new ScheduledActiveProducer<>() {
            @Override
            protected T produceData() {
                return contextFunction.apply(getContext());
            }
        };
    }

    public static <T> ScheduledActiveProducer<T> activeProducer(Supplier<T> contextSupplier) {
        return new ScheduledActiveProducer<>() {
            @Override
            protected T produceData() {
                return contextSupplier.get();
            }
        };
    }

    public static ScheduledActiveProducerConfig activeProducerConfig(int fixedRate, TimeUnit timeUnit)
    {
        return new ScheduledActiveProducerConfig(fixedRate, timeUnit);
    }
}
