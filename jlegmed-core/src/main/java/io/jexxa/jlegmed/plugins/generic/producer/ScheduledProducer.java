package io.jexxa.jlegmed.plugins.generic.producer;


import io.jexxa.common.drivingadapter.scheduler.ScheduledFixedRate;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ScheduledProducer<T> extends ActiveProducer<T>  {
    private int fixedRate;
    private TimeUnit timeUnit;

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
        scheduler.register(new ScheduledFixedRate(this::execute,0, fixedRate, timeUnit ));
        scheduler.start();
    }

    public ScheduledProducer<T> fixedRate(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
        return this;
    }


    @Override
    public void stop() {
        scheduler.stop();
    }

    private void execute()
    {
        outputPipe().forward(generateData());
    }

    protected abstract T generateData();

    public record Schedule(int fixedRate, TimeUnit timeUnit){}


    public static <T> ScheduledProducer<T> scheduledProducer(BiFunction<FilterContext, Class<T>, T> biFunction) {
        return new ScheduledProducer<>() {
            @Override
            protected T generateData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }
    public static <T> ScheduledProducer<T> scheduledProducer(Function<FilterContext, T> contextFunction) {
        return new ScheduledProducer<>() {
            @Override
            protected T generateData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(Supplier<T> contextSupplier) {
        return new ScheduledProducer<>() {
            @Override
            protected T generateData() {
                return contextSupplier.get();
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(
            BiFunction<FilterContext, Class<T>, T> biFunction, Schedule schedule
    ) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T generateData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(Function<FilterContext, T> contextFunction, Schedule schedule) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T generateData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(Supplier<T> contextSupplier, Schedule schedule) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T generateData() {
                return contextSupplier.get();
            }
        };
    }

    public static Schedule schedule(int fixedRate, TimeUnit timeUnit)
    {
        return new Schedule(fixedRate, timeUnit);
    }
}
