package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FlowGraph<T> {
    private final Context context;

    private Producer<T> producer;
    private final String flowGraphID;
    private final List<Filter> filterList = new ArrayList<>();

    public FlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.context = new Context(properties);
    }

    public String getFlowGraphID()
    {
        return flowGraphID;
    }

    public Context getContext()
    {
        return context;
    }

    public void init() {
        filterList.forEach(Filter::init);
    }

    public void start() {
        filterList.forEach(Filter::start);
    }

    public void stop() {
        filterList.forEach(Filter::start);
    }

    public void deInit() {
        filterList.forEach(Filter::init);
    }

    public void setProducer(Producer<T> producer)
    {
        this.producer = producer;
        producer.setContext(getContext());
    }

    public void addFilter(Filter filter)
    {
        filterList.add(filter);
    }

    protected Producer<T> getProducer()
    {
        return producer;
    }


}
