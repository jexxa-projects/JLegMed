package io.jexxa.jlegmed.core.filter.scheduler;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;

public class RepeatScheduler implements FilterScheduler {
    private final int times;
    private Runnable passiveProducer;

    int fixedRate = 0;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private ScheduledExecutorService executorService;

    private int iterationCounter = 0;

    public RepeatScheduler(int times) {
        this.times = times;
    }

    public RepeatScheduler(int times, int fixedRate, TimeUnit timeUnit) {
        this.times = times;
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }


    @Override
    public void start() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        if (fixedRate == 0) {
            executorService.schedule(this::iterateTimes, 0, timeUnit);
        } else {
            executorService.scheduleAtFixedRate(this::iterateAtRate, 0, fixedRate, timeUnit);
        }
    }

    public void iterateAtRate() {
        try {
            if (iterationCounter >= times) {
                executorService.shutdown();
                return;
            }

            InvocationManager
                    .getInvocationHandler(passiveProducer)
                    .invoke(passiveProducer, passiveProducer::run);
            ++iterationCounter;
        } catch (InvocationTargetRuntimeException e) {
            getLogger(this.getClass()).error(e.getTargetException().getMessage());
            getLogger(this.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
        } catch (Exception e) {
            getLogger(this.getClass()).error(e.getMessage());
            getLogger(this.getClass()).debug(e.getMessage(), e);
        }
    }


    public void iterateTimes() {
        try {
            while (iterationCounter < times) {
                InvocationManager
                        .getInvocationHandler(passiveProducer)
                        .invoke(passiveProducer, passiveProducer::run);
                ++iterationCounter;
            }

            executorService.shutdown();
        } catch (InvocationTargetRuntimeException e) {
            getLogger(this.getClass()).error(e.getTargetException().getMessage());
            getLogger(this.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
        } catch (Exception e) {
            getLogger(this.getClass()).error(e.getMessage());
            getLogger(this.getClass()).debug(e.getMessage(), e);
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

    @Override
    public void schedule(Runnable passiveProducer) {
        this.passiveProducer = passiveProducer;
    }


}
