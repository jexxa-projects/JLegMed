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
    private int fixedRate = 5;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private final Scheduler scheduler = new Scheduler();


    @Override
    public void init() {
        var filterConfig = filterContext().getFilterConfig(Schedule.class);
        if ( filterConfig.isPresent()) {
            var schedule = filterConfig.get();
            fixedRate = schedule.fixedRate();
            timeUnit = schedule.timeUnit();
        }
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

    public ScheduledProducer<T> withInterval(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        return this;
    }

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

    public static Schedule schedule(int fixedRate, TimeUnit timeUnit)
    {
        return new Schedule(fixedRate, timeUnit);
    }
}
