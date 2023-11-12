package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;
import io.jexxa.jlegmed.common.component.scheduler.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public class RepeatScheduler implements FlowGraphScheduler{
    private final int times;
    private FlowGraph flowGraph;

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

    public void register(FlowGraph flowGraph) {
        this.flowGraph = flowGraph;
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
                    .getInvocationHandler(flowGraph)
                    .invoke(flowGraph, (SerializableRunnable) flowGraph::iterate);
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
                        .getInvocationHandler(flowGraph)
                        .invoke(flowGraph, (SerializableRunnable) flowGraph::iterate);
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


}
