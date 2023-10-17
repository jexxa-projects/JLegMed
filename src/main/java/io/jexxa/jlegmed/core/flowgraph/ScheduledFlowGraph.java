package io.jexxa.jlegmed.core.flowgraph;


import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.jlegmed.common.scheduler.IScheduled;
import io.jexxa.jlegmed.common.scheduler.Scheduler;
import io.jexxa.jlegmed.core.producer.Producer;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.core.producer.TypedProducer;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.logger.SLF4jLogger.getLogger;

public final class ScheduledFlowGraph extends AbstractFlowGraph {
    private final Scheduler scheduler = new Scheduler();
    private final FlowGraphSchedule flowGraphSchedule;

    private Producer producer;
    private Class<?> expectedData;

    public ScheduledFlowGraph(String flowGraphID, Properties properties, int fixedRate, TimeUnit timeUnit)
    {
        super(flowGraphID, properties);
        this.flowGraphSchedule = new FlowGraphSchedule(this, fixedRate, timeUnit);
    }

    public <T> TypedProducer<T> receive(Class<T> expectedData)
    {
        this.expectedData = expectedData;
        return new TypedProducer<>(this);
    }

    public <T extends ProducerURL> T from(T producerURL) {
        this.producer = producerURL.init(this);
        return producerURL;
    }

    public FlowGraph generatedWith(Producer producer) {
        try {
            this.producer = producer;
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return this;
    }


    public void start()
    {
        scheduler.register(flowGraphSchedule);
        scheduler.start();
    }

    public void stop()
    {
        scheduler.stop();
    }


    private static class FlowGraphSchedule implements IScheduled
    {
        ScheduledFlowGraph flowGraph;
        private final int fixedRate;
        private final TimeUnit timeUnit;

        FlowGraphSchedule(ScheduledFlowGraph flowGraph, int fixedRate, TimeUnit timeUnit)
        {
            this.flowGraph = flowGraph;
            this.fixedRate = fixedRate;
            this.timeUnit = timeUnit;
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
            try {
                InvocationManager
                        .getInvocationHandler(flowGraph)
                        .invoke(flowGraph, flowGraph::iterateFlowGraph);
            }
            catch (InvocationTargetRuntimeException e) {
                getLogger(this.getClass()).error(e.getTargetException().getMessage());
                getLogger(this.getClass()).debug(e.getTargetException().getMessage(), e.getTargetException());
            }
            catch (Exception e)
            {
                getLogger(this.getClass()).error(e.getMessage());
                getLogger(this.getClass()).debug(e.getMessage(), e);
            }
        }


    }

    private void iterateFlowGraph()
    {
        var result = producer.produce(expectedData, getContext());
        if ( result != null) {
            processMessage(new Content(result));
        }
    }


}
