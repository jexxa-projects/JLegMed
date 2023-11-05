package io.jexxa.jlegmed.core.flowgraph;


import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.jlegmed.common.component.scheduler.IScheduled;
import io.jexxa.jlegmed.common.component.scheduler.Scheduler;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public final class ScheduledFlowGraph<T> extends FlowGraph<T> {
    private final Scheduler scheduler = new Scheduler();
    private final FixedRateScheduler fixedRateScheduler;

    public ScheduledFlowGraph(String flowGraphID, Properties properties, int fixedRate, TimeUnit timeUnit)
    {
        super(flowGraphID, properties);
        this.fixedRateScheduler = new FixedRateScheduler(this, fixedRate, timeUnit);
    }


    @Override
    public void start()
    {
        super.start();
        scheduler.register(fixedRateScheduler);
        scheduler.start();
    }

    @Override
    public void stop()
    {
        scheduler.stop();
        super.stop();
    }

    private void iterateFlowGraph()
    {
        producer().start();
    }

    private record FixedRateScheduler(ScheduledFlowGraph<?> flowGraph, int fixedRate, TimeUnit timeUnit) implements IScheduled
    {
        @Override
        public void execute() {
            try {
                InvocationManager
                        .getInvocationHandler(flowGraph)
                        .invoke(flowGraph, flowGraph::iterateFlowGraph);
            } catch (InvocationTargetRuntimeException e) {
                getLogger(this.getClass()).error(e.getTargetException().getMessage());
                getLogger(this.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
            } catch (Exception e) {
                getLogger(this.getClass()).error(e.getMessage());
                getLogger(this.getClass()).debug(e.getMessage(), e);
            }
        }
    }
}
