package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;

public class ProducerBuilder<T> {
    private final Class<T> sourceType;
    private final FlowGraph flowGraph;

    ProducerBuilder(FlowGraph flowGraph, Class<T> sourceType) {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
    }

    public Binding<T> from(Function<FilterContext, T> function) {
        var typedProducer = producer(function);
        typedProducer.producingType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new Binding<>(typedProducer, typedProducer.outputPipe(), flowGraph);
    }

    public Binding<T> from(BiFunction<FilterContext, Class<T>, T> biFunction) {
        var typedProducer = producer(biFunction);
        typedProducer.producingType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new Binding<>(typedProducer, typedProducer.outputPipe(), flowGraph);
    }

    public Binding<T> from(Supplier<T> supplier) {
        var typedProducer = producer(supplier);
        typedProducer.producingType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new Binding<>(typedProducer, typedProducer.outputPipe(), flowGraph);
    }

    public Binding<T> from(PassiveProducer<T> producer) {
        producer.producingType(sourceType);
        flowGraph.setProducer(producer);

        return new Binding<>(producer, producer.outputPipe(), flowGraph);
    }

}
