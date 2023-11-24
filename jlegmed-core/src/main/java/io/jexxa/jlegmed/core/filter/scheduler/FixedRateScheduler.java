package io.jexxa.jlegmed.core.filter.scheduler;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;

import io.jexxa.commons.wrapper.component.scheduler.IScheduled;
import io.jexxa.commons.wrapper.component.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterScheduler;

import java.util.concurrent.TimeUnit;

import static io.jexxa.commons.wrapper.logger.SLF4jLogger.getLogger;


public class FixedRateScheduler implements FilterScheduler, IScheduled {
    private final Scheduler scheduler = new Scheduler();
    private final int fixedRate;
    private final TimeUnit timeUnit;
    private Runnable passiveProducer;

    public FixedRateScheduler(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    @Override
    public void start()
    {
        scheduler.register(this);

        scheduler.start();
    }

    @Override
    public void stop()
    {
        scheduler.stop();
    }

    @Override
    public void schedule(Runnable passiveProducer) {
        this.passiveProducer = passiveProducer;
    }

    @Override
    public int fixedRate()
    {
        return fixedRate;
    }

    @Override
    public TimeUnit timeUnit()
    {
        return timeUnit;
    }

    @Override
    public void execute() {
        try {
            InvocationManager
                    .getInvocationHandler(passiveProducer)
                    .invoke(passiveProducer, passiveProducer::run);
        } catch (InvocationTargetRuntimeException e) {
            getLogger(this.getClass()).error(e.getTargetException().getMessage());
            getLogger(this.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
        } catch (Exception e) {
            getLogger(this.getClass()).error(e.getMessage());
            getLogger(this.getClass()).debug(e.getMessage(), e);
        }
    }
}
