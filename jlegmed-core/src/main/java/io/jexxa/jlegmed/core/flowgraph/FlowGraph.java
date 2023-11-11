package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.jexxa.adapterapi.invocation.InvocationManager.getRootInterceptor;

public class FlowGraph {
    private final Properties properties;

    private Producer<?> producer;
    private final String flowGraphID;
    private final List<Filter> filterList = new ArrayList<>();
    private final List<Processor<?,?>> processorList = new ArrayList<>();

    public FlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.properties = properties;
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

    public void start() {
        filterList.forEach(Filter::init);
        filterList.forEach(Filter::start);
    }

    public void stop() {
        filterList.forEach(Filter::stop);
        filterList.forEach(Filter::deInit);
    }

    public void producer(Producer<?> producer)
    {
        this.producer = producer;
        filterList.add(producer);
    }
    public void produceData()
    {
         producer.produceData();
    }

    public void addProcessor(Processor<?,?> processor)
    {
        filterList.add(processor);
        processorList.add(processor);
    }

    public void monitorPipes(BeforeInterceptor interceptor)
    {
        getRootInterceptor(producer.outputPipe()).registerBefore(interceptor);

        processorList.stream()
                .map(Processor::outputPipe)
                .forEach( element -> getRootInterceptor(element).registerBefore(interceptor));
    }
}
