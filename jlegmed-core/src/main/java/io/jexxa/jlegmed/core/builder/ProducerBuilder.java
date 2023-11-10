package io.jexxa.jlegmed.core.builder;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;

public class ProducerBuilder<T> {
    private final Class<T> sourceType;
    private final FlowGraph<T> flowGraph;

    ProducerBuilder(FlowGraph<T> flowGraph, Class<T> sourceType) {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
    }

    public Binding<T> from(Function<FilterContext, T> function) {
        var typedProducer = producer(function);
        typedProducer.producingType(sourceType);
        flowGraph.producer(typedProducer);

        return new Binding<>(typedProducer, typedProducer.outputPipe(), flowGraph);
    }

    public Binding<T> from(BiFunction<FilterContext, Class<T>, T> biFunction) {
        var typedProducer = producer(biFunction);
        typedProducer.producingType(sourceType);
        flowGraph.producer(typedProducer);

        return new Binding<>(typedProducer, typedProducer.outputPipe(), flowGraph);
    }

    public Binding<T> from(Supplier<T> supplier) {
        var typedProducer = producer(supplier);
        typedProducer.producingType(sourceType);
        flowGraph.producer(typedProducer);

        return new Binding<>(typedProducer, typedProducer.outputPipe(), flowGraph);
    }

    public Binding<T> from(Producer<T> producer) {
        producer.producingType(sourceType);
        flowGraph.producer(producer);

        return new Binding<>(producer, producer.outputPipe(), flowGraph);
    }

}
