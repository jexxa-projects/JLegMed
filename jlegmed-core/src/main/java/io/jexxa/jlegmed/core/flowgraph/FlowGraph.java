package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static io.jexxa.adapterapi.invocation.InvocationManager.getRootInterceptor;

public class FlowGraph {
    private final Properties properties;
    private Producer<?> producer;
    private final String flowGraphID;
    private final List<Filter> filterList = new ArrayList<>();
    private final List<Processor<?,?>> processorList = new ArrayList<>();

    private final FlowGraphScheduler flowGraphScheduler = new FlowGraphScheduler();


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
        filterList.stream().filter(element -> element != producer).forEach(Filter::start);
        producer.start();

        flowGraphScheduler.start();

        return this;
    }

    public void stop() {
        flowGraphScheduler.stop();
        filterList.forEach(Filter::stop);
        filterList.forEach(Filter::deInit);
    }

    public void setProducer(Producer<?> producer)
    {
        this.producer = producer;
        filterList.add(producer);
    }

    public void addProcessor(Processor<?, ?> processor)
    {
        if (!processorList.contains(processor)) {
            filterList.add(processor);
            processorList.add(processor);
        }
    }

    public FlowGraphScheduler getScheduler() {
        return flowGraphScheduler;
    }

    public <T, U> FlowGraph connect(Producer<T> producer, Processor<T,U> processor)
    {
        producer.outputPipe().connectTo(processor.inputPipe());

        setProducer(producer);
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

    public List<FilterProperties> filterProperties()
    {
        return filterList.stream().map( Filter::filterProperties ).toList();
    }

    public void waitUntilFinished()
    {
        flowGraphScheduler.waitUntilFinished();
    }

}
