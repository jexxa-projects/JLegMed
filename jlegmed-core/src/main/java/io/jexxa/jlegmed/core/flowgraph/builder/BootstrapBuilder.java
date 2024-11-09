package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.Properties;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class BootstrapBuilder {
    private final FlowGraph flowGraph;

    public BootstrapBuilder(String flowGraphID, Properties properties)
    {
        this.flowGraph = new FlowGraph(flowGraphID, properties);
    }
    public RepeatBuilder repeat(int times)
    {
        return new RepeatBuilder(times, flowGraph);
    }

    public Binding<Void, Void> execute(SerializableConsumer<FilterContext> consumer)
    {
        return new ProducerBuilder<>(flowGraph, Void.class, 1, NANOSECONDS, 1 ).from(FunctionalProducer.consumer(consumer));
    }

    public FlowGraph getFlowGraph()
    {
        return flowGraph;
    }

}
