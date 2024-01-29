package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.function.Consumer;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class BootstrapBuilder {
    private final FlowGraph flowGraph;

    public BootstrapBuilder(String flowGraphID, JLegMed jLegMed)
    {
        this.flowGraph = new FlowGraph(flowGraphID, jLegMed.getProperties());
        jLegMed.addBootstrapFlowGraph(flowGraph);
    }
    public RepeatBuilder repeat(int times)
    {
        return new RepeatBuilder(times, flowGraph);
    }

    public Binding<Void> execute(Consumer<FilterContext> consumer)
    {
        return new ProducerBuilder<>(flowGraph, Void.class, 1, NANOSECONDS, 1 ).from(producer(consumer));
    }


}
