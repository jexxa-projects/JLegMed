package io.jexxa.jlegmed.common.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;


public class Scheduler
{
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private final List<IScheduled> scheduledMethods = new ArrayList<>();

    public void register(IScheduled iScheduled) {
        scheduledMethods.add(iScheduled);
    }

    public void start() {
        scheduledMethods.forEach(this::registerScheduledMethods);
    }

    private void registerScheduledMethods(IScheduled iScheduled)
    {
        if (iScheduled.fixedRate() >= 0) {
            executorService.scheduleAtFixedRate(
                    iScheduled::execute,
                    iScheduled.initialDelay(),
                    iScheduled.fixedRate(),
                    iScheduled.timeUnit());
        } else {
            executorService.scheduleWithFixedDelay(
                    iScheduled::execute,
                    iScheduled.initialDelay(),
                    iScheduled.fixedDelay(),
                    iScheduled.timeUnit());
        }
    }


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
