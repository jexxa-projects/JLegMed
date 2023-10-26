package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;

public class ProducerBinding<T> {
    private final Class<T> sourceType;
    private final FlowGraph<T> flowGraph;

    public ProducerBinding(FlowGraph<T> flowGraph, Class<T> sourceType)
    {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
    }
    public ProcessorBinding<T> from(Function<Context, T> function) {
        var typedProducer = producer(function);
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);
        return new ProcessorBinding<>(typedProducer.getOutputPipe(), typedProducer);
    }

    public ProcessorBinding<T> from(BiFunction<Context, Class<T>, T> biFunction) {
        var typedProducer = producer(biFunction);
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new ProcessorBinding<>(typedProducer.getOutputPipe(), typedProducer);
    }

    public ProcessorBinding<T> from(Supplier<T> supplier) {
        var typedProducer = producer(supplier);
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new ProcessorBinding<>(typedProducer.getOutputPipe(), typedProducer);
    }

    public ProcessorBinding<T> from(Producer<T> producer) {
        producer.setType(sourceType);
        flowGraph.setProducer(producer);
        return new ProcessorBinding<>(producer.getOutputPipe(), producer);
    }

}
