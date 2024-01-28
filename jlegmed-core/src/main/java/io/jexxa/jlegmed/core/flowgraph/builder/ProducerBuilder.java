package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.core.filter.producer.PipedProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;

public class ProducerBuilder<T> {
    private final Class<T> sourceType;
    private final long fixedRate;
    private final TimeUnit timeUnit;
    private final FlowGraph flowGraph;
    private long maxIteration = -1;

    ProducerBuilder(FlowGraph flowGraph, Class<T> sourceType, long fixedRate, TimeUnit timeUnit) {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    ProducerBuilder(FlowGraph flowGraph, Class<T> sourceType, long fixedRate, TimeUnit timeUnit, long maxIteration) {
        this(flowGraph, sourceType, fixedRate, timeUnit);
        this.maxIteration = maxIteration;
    }


    public Binding<T> from(Function<FilterContext, T> function) {
        return configureScheduler(producer(function));
    }

    public Binding<T> from(PipedProducer<T> function) {
        return configureScheduler(producer(function));
    }


    public Binding<T> from(Supplier<T> supplier) {
        return configureScheduler(producer(supplier));
    }

    public Binding<T> from(PassiveProducer<T> producer) {
        producer.producingType(sourceType);
        return configureScheduler(producer);
    }

    private Binding<T> configureScheduler(PassiveProducer<T> producer)
    {
        flowGraph.setProducer(producer);
        if (maxIteration < 0) {
            flowGraph.getScheduler().configureFixedRate(producer,fixedRate, timeUnit);
        } else {
            flowGraph.getScheduler().configureRepeatedRate( producer, maxIteration,  fixedRate, timeUnit);
        }

        return new Binding<>(producer, producer.outputPipe(), flowGraph);
    }
}
