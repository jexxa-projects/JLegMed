package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static io.jexxa.adapterapi.invocation.InvocationManager.getRootInterceptor;

public class FlowGraph {
    private final Properties properties;

    private ActiveProducer<?> activeProducer;
    private PassiveProducer<?> passiveProducer;
    private Producer<?> producer;
    private final String flowGraphID;
    private final List<Filter> filterList = new ArrayList<>();
    private final List<Processor<?,?>> processorList = new ArrayList<>();


    public FlowGraph(String flowGraphID)
    {
        this(flowGraphID, new Properties());
    }

    public FlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.properties = Objects.requireNonNull(properties);
    }


    @SuppressWarnings("unused")
    public String flowGraphID()
    {
        return flowGraphID;
    }

    public Properties properties()
    {
        return properties;
    }

    public FlowGraph start() {
        filterList.forEach(Filter::init);
        filterList.forEach(Filter::start);

        return this;
    }

    public void stop() {
        filterList.forEach(Filter::stop);
        filterList.forEach(Filter::deInit);
    }

    public void setProducer(ActiveProducer<?> producer)
    {
        this.producer = producer;
        this.activeProducer = producer;
        filterList.add(producer);
    }

    public void setProducer(PassiveProducer<?> producer)
    {
        this.producer = producer;
        this.passiveProducer = producer;
        filterList.add(producer);
    }

    public void addProcessor(Processor<?,?> processor)
    {
        if (!processorList.contains(processor)) {
            filterList.add(processor);
            processorList.add(processor);
        }
    }

    public FlowGraph iterate()
    {
         return iterate(1);
    }

    public FlowGraph iterate(int iterationCount)
    {
        for (int i = 0; i < iterationCount; i++) {
            passiveProducer.produceData();
        }
        return this;
    }

    public <T, U> FlowGraph connect(ActiveProducer<T> producer, Processor<T,U> processor)
    {
        activeProducer = producer;
        return internalConnect(producer, processor);
    }

    public <T, U> FlowGraph connect(PassiveProducer<T> producer, Processor<T,U> processor)
    {
        passiveProducer = producer;
        return internalConnect(producer, processor);
    }


    private <T, U> FlowGraph internalConnect(Producer<T> producer, Processor<T,U> processor)
    {
        producer.outputPipe().connectTo(processor.inputPipe());

        addProcessor(processor);

        return this;
    }

    public <T, U, V> FlowGraph connect(Processor<T, U> predecessor, Processor<U,V> processor)
    {
        predecessor.outputPipe().connectTo(processor.inputPipe());
        addProcessor(predecessor);
        addProcessor(processor);

        return this;
    }
    public void monitorPipes(BeforeInterceptor interceptor)
    {
        getRootInterceptor(producer.outputPipe()).registerBefore(interceptor);

        processorList.stream()
                .map(Processor::outputPipe)
                .forEach( element -> getRootInterceptor(element).registerBefore(interceptor));
    }
}
