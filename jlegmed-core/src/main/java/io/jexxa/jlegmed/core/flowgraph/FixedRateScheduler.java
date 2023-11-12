package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;
import io.jexxa.jlegmed.common.component.scheduler.IScheduled;
import io.jexxa.jlegmed.common.component.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public class FixedRateScheduler implements FlowGraphScheduler, IScheduled {
    private final Scheduler scheduler = new Scheduler();
    private FlowGraph flowGraph;
    private final int fixedRate;
    private final TimeUnit timeUnit;

    public FixedRateScheduler(int fixedRate, TimeUnit timeUnit)
    {
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    public void register(FlowGraph flowGraph)
    {
        this.flowGraph = flowGraph;
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
                    .getInvocationHandler(flowGraph)
                    .invoke(flowGraph, (SerializableRunnable) flowGraph::iterate);
        } catch (InvocationTargetRuntimeException e) {
            getLogger(this.getClass()).error(e.getTargetException().getMessage());
            getLogger(this.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
        } catch (Exception e) {
            getLogger(this.getClass()).error(e.getMessage());
            getLogger(this.getClass()).debug(e.getMessage(), e);
        }
    }
}
