package io.jexxa.jlegmed.core.flowgraph;


import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.jlegmed.common.scheduler.IScheduled;
import io.jexxa.jlegmed.common.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.logger.SLF4jLogger.getLogger;

public final class ScheduledFlowGraph<T> extends FlowGraph {
    private final Scheduler scheduler = new Scheduler();
    private final FixedRateScheduler fixedRateScheduler;

    private TypedProducer<T> producer = new TypedProducer<>();
    private Class<T> expectedData;

    public ScheduledFlowGraph(String flowGraphID, Properties properties, int fixedRate, TimeUnit timeUnit)
    {
        super(flowGraphID, properties);
        this.fixedRateScheduler = new FixedRateScheduler(this, fixedRate, timeUnit);
    }

    public void receive(Class<T> expectedData)
    {
        this.expectedData = expectedData;
        producer.setType(expectedData);
    }

    public <U extends TypedProducer<T>> U from(U typedProducer) {
        this.producer = typedProducer;
        this.producer.setType(expectedData);
        return typedProducer;
    }

    public TypedProducer<T> getProducer()
    {
        return producer;
    }

    @Override
    public void start()
    {
        producer.start();
        scheduler.register(fixedRateScheduler);
        scheduler.start();
    }

    @Override
    public void stop()
    {
        producer.stop();
        scheduler.stop();
    }

    private void iterateFlowGraph()
    {
        producer.produceData(expectedData, getContext());
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
