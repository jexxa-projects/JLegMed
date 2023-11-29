package io.jexxa.jlegmed.plugins.generic.producer;



import io.jexxa.common.drivingadapter.scheduler.IScheduled;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ScheduledProducer<T> extends ActiveProducer<T> implements IScheduled {
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
        scheduler.register(this);
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
        outputPipe().forward(generateData());
    }


    protected abstract T generateData();

    public record Schedule(int fixedRate, TimeUnit timeUnit){}


    public static <T> ScheduledProducer<T> activeProducer(BiFunction<FilterContext, Class<T>, T> biFunction) {
        return new ScheduledProducer<>() {
            @Override
            protected T generateData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }
    public static <T> ScheduledProducer<T> activeProducer(Function<FilterContext, T> contextFunction) {
        return new ScheduledProducer<>() {
            @Override
            protected T generateData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(Supplier<T> contextSupplier) {
        return new ScheduledProducer<>() {
            @Override
            protected T generateData() {
                return contextSupplier.get();
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(
            BiFunction<FilterContext, Class<T>, T> biFunction, Schedule schedule
    ) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T generateData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(Function<FilterContext, T> contextFunction,Schedule schedule) {
        return new ScheduledProducer<>(schedule) {
            @Override
            protected T generateData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> activeProducer(Supplier<T> contextSupplier, Schedule schedule) {
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
