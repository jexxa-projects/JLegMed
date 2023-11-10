package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FlowGraph<T> {
    private final Properties properties;

    private Producer<T> producer;
    private final String flowGraphID;
    private final List<Filter> filterList = new ArrayList<>();
    private final List<Processor<?,?>> processorList = new ArrayList<>();

    public FlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.properties = properties;
    }

    public String flowGraphID()
    {
        return flowGraphID;
    }

    public Properties properties()
    {
        return properties;
    }

    public void start() {
        filterList.forEach(Filter::init);
        filterList.forEach(Filter::start);
    }
    public List<Filter> getFilterList()
    {
        return filterList;
    }

    public void stop() {
        filterList.forEach(Filter::stop);
        filterList.forEach(Filter::deInit);
    }

    public void producer(Producer<T> producer)
    {
        this.producer = producer;
        filterList.add(producer);
    }
    public Producer<T> producer()
    {
        return producer;
    }

    public void addProcessor(Processor<?,?> processor)
    {
        filterList.add(processor);
        processorList.add(processor);
    }

    public List<Processor<?,?>> processorList()
    {
        return processorList;
    }
}
