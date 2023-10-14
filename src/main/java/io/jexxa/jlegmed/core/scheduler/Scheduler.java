package io.jexxa.jlegmed.core.scheduler;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.logger.SLF4jLogger.getLogger;


public class Scheduler implements IDrivingAdapter
{
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private final List<IScheduled> scheduledMethods = new ArrayList<>();

    @Override
    public void register(Object port) {
        var validatedPort = validateSchedulerConfiguration(port);
        scheduledMethods.add(validatedPort);
    }

    @Override
    public void start() {
        scheduledMethods.forEach(this::registerScheduledMethods);
    }

    private void registerScheduledMethods(IScheduled iScheduled)
    {
        if (iScheduled.fixedRate() >= 0) {
            executorService.scheduleAtFixedRate(
                    () -> invoke(iScheduled),
                    iScheduled.initialDelay(),
                    iScheduled.fixedRate(),
                    iScheduled.timeUnit());
        } else {
            executorService.scheduleWithFixedDelay(
                    () -> invoke(iScheduled),
                    iScheduled.initialDelay(),
                    iScheduled.fixedDelay(),
                    iScheduled.timeUnit());
        }
    }

    private void invoke(IScheduled iScheduled )
    {
        var invocationHandler = InvocationManager.getInvocationHandler(iScheduled);

        try {
            invocationHandler.invoke(iScheduled, iScheduled::execute);
        }
        catch (InvocationTargetRuntimeException e) {
            getLogger(iScheduled.getClass()).error(e.getTargetException().getMessage());
            getLogger(iScheduled.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
        }
        catch (Exception e)
        {
            getLogger(iScheduled.getClass()).error(e.getMessage());
            getLogger(iScheduled.getClass()).debug(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        executorService.shutdown();
        try
        {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS))
            {
                executorService.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            executorService.shutdownNow();
            getLogger(Scheduler.class).warn("ExecutorService could not be stopped -> Force shutdown.", e);
            Thread.currentThread().interrupt();
        }
    }

    private IScheduled validateSchedulerConfiguration(Object object)
    {
        Objects.requireNonNull(object);
        return getScheduled(object);
    }

    private static IScheduled getScheduled(Object object) {
        var scheduledConfiguration = (IScheduled) object;

        if ((scheduledConfiguration.fixedDelay() < 0 && scheduledConfiguration.fixedRate() < 0)
            || (scheduledConfiguration.fixedDelay() >= 0 && scheduledConfiguration.fixedRate() >= 0))
        {
            throw new IllegalArgumentException(
                    String.format("Given method %s::%s does not provide a valid  value for `fixedInterval` or `fixedDelay` in @Scheduled (exact one of these values must be >=0)!"
                            , object.getClass().getSimpleName()
                            , object.getClass().getName()));
        }
        return scheduledConfiguration;
    }

}
