package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.producer.BiFunctionProducer;
import io.jexxa.jlegmed.core.filter.producer.FunctionProducer;
import io.jexxa.jlegmed.core.filter.producer.SupplierProducer;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProducerBinding<T> {
    private final Class<T> sourceType;
    private final FlowGraph<T> flowGraph;

    public ProducerBinding(FlowGraph<T> flowGraph, Class<T> sourceType)
    {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
    }
    public ProcessorBinding<T> from(Function<Context, T> function) {
        var typedProducer = new FunctionProducer<>(function);
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);
        return new ProcessorBinding<>(typedProducer.getOutputPipe(), typedProducer);
    }

    public ProcessorBinding<T> from(BiFunction<Context, Class<T>, T> biFunction) {
        var typedProducer = new BiFunctionProducer<>(biFunction);
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new ProcessorBinding<>(typedProducer.getOutputPipe(), typedProducer);
    }

    public ProcessorBinding<T> from(Supplier<T> supplier) {
        var typedProducer = new SupplierProducer<>(supplier);
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);

        return new ProcessorBinding<>(typedProducer.getOutputPipe(), typedProducer);
    }

    public ProcessorBinding<T> from(TypedProducer<T> producer) {
        producer.setType(sourceType);
        flowGraph.setProducer(producer);
        return new ProcessorBinding<>(producer.getOutputPipe(), producer);
    }

}
