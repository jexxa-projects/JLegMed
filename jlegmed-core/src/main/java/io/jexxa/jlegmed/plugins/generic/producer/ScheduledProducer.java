package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.common.component.scheduler.IScheduled;
import io.jexxa.jlegmed.common.component.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ScheduledProducer<T> extends Producer<T> implements IScheduled {
    private final int fixedRate;
    private final TimeUnit timeUnit;

    private final Scheduler scheduler = new Scheduler();

    protected ScheduledProducer(Schedule  schedule)
    {
        this.fixedRate = schedule.fixedRate;
        this.timeUnit = schedule.timeUnit;
    }

    protected ScheduledProducer()
    {
        this(schedule(10, TimeUnit.MILLISECONDS));
    }

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
        outputPipe().forward(produceData());
    }

    protected abstract T produceData();

    public record Schedule(int fixedRate, TimeUnit timeUnit){}


    public static <T> ScheduledProducer<T> activeProducer(BiFunction<FilterContext, Class<T>, T> biFunction) {
        return new ScheduledProducer<>() {
            @Override
            protected T produceData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }
    public static <T> ScheduledProducer<T> activeProducer(Function<FilterContext, T> contextFunction) {
        return new ScheduledProducer<>() {
            @Override
            protected T produceData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(Supplier<T> contextSupplier) {
        return new ScheduledProducer<>() {
            @Override
            protected T produceData() {
                return contextSupplier.get();
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(
            BiFunction<FilterContext, Class<T>, T> biFunction, Schedule schedule
    ) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T produceData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(Function<FilterContext, T> contextFunction,Schedule schedule) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T produceData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(Supplier<T> contextSupplier, Schedule schedule) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T produceData() {
                return contextSupplier.get();
            }
        };
    }

    public static Schedule schedule(int fixedRate, TimeUnit timeUnit)
    {
        return new Schedule(fixedRate, timeUnit);
    }
}
