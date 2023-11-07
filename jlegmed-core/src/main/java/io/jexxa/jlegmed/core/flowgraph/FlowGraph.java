package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class FlowGraph<T> {
    private final Properties properties;

    private Producer<T> producer;
    private final String flowGraphID;
    private final List<Filter> filterList = new ArrayList<>();

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
        //We iterate in reverse order to ensure that filters are started before producer/predecessors
        var reverseOrderOfFilters = filterList;
        Collections.reverse(reverseOrderOfFilters);

        reverseOrderOfFilters.forEach(Filter::init);
        reverseOrderOfFilters.forEach(Filter::start);
    }

    public void stop() {
        filterList.forEach(Filter::stop);
        filterList.forEach(Filter::deInit);
    }

    public void producer(Producer<T> producer)
    {
        this.producer = producer;
    }
    protected Producer<T> producer()
    {
        return producer;
    }

    public void addFilter(Filter filter)
    {
        filterList.add(filter);
    }
}
