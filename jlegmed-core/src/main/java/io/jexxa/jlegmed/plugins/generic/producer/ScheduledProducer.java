package io.jexxa.jlegmed.plugins.generic.producer;


import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;
import io.jexxa.common.drivingadapter.scheduler.ScheduledFixedRate;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

import java.util.concurrent.TimeUnit;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.classNameFromLambda;

public abstract class ScheduledProducer<T> extends ActiveProducer<T>  {
    private final Scheduler scheduler = new Scheduler();
    private int fixedRate;
    private TimeUnit timeUnit;

    private final String name;

    protected ScheduledProducer(Schedule  schedule, String name, Class<?> classFromLambda)
    {
        super(classFromLambda);
        this.name = name;
        this.fixedRate = schedule.fixedRate;
        this.timeUnit = schedule.timeUnit;
    }

    protected ScheduledProducer(String name, Class<?> classFromLambda)
    {
        this(schedule(10, TimeUnit.MILLISECONDS), name, classFromLambda);
    }

    @Override
    public String name() {
        return name;
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
        T generatedData = null;
        try {
            generatedData = generateData();
            outputPipe().forward(generatedData);
        } catch (ProcessingException e) {
            errorPipe().forward(new ProcessingError<>(generatedData, e));
        } catch (RuntimeException e) {
            errorPipe().forward(new ProcessingError<>(generatedData, new ProcessingException(this.name(), name() + " could not generate data", e)));
        }
    }

    protected abstract T generateData();

    public record Schedule(int fixedRate, TimeUnit timeUnit){}


    public static <T> ScheduledProducer<T> scheduledProducer(SerializableBiFunction<FilterContext, Class<T>, T> biFunction) {
        return new ScheduledProducer<>(
                filterNameFromLambda(biFunction),
                classNameFromLambda(biFunction))
        {
            @Override
            protected T generateData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }
    public static <T> ScheduledProducer<T> scheduledProducer(SerializableFunction<FilterContext, T> contextFunction) {
        return new ScheduledProducer<>(
                filterNameFromLambda(contextFunction),
                classNameFromLambda(contextFunction))
        {
            @Override
            protected T generateData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(SerializableSupplier<T> contextSupplier) {
        return new ScheduledProducer<>(
                filterNameFromLambda(contextSupplier),
                classNameFromLambda(contextSupplier))
        {
            @Override
            protected T generateData() {
                return contextSupplier.get();
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(
            SerializableBiFunction<FilterContext, Class<T>, T> biFunction, Schedule schedule
    ) {
        return new ScheduledProducer<>(schedule,
                filterNameFromLambda(biFunction),
                classNameFromLambda(biFunction))
        {
            @Override
            protected T generateData() {
                return biFunction.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(SerializableFunction<FilterContext, T> contextFunction, Schedule schedule) {
        return new ScheduledProducer<>(schedule,
                filterNameFromLambda(contextFunction),
                classNameFromLambda(contextFunction))
        {
            @Override
            protected T generateData() {
                return contextFunction.apply(filterContext());
            }
        };
    }

    public static <T> ScheduledProducer<T> scheduledProducer(SerializableSupplier<T> contextSupplier, Schedule schedule) {
        return new ScheduledProducer<>(schedule,
                filterNameFromLambda(contextSupplier),
                classNameFromLambda(contextSupplier))
        {
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
