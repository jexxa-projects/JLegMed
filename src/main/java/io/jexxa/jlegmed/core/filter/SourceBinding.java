package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class SourceBinding <T> {
    private final Class<T> sourceType;
    private final FlowGraph<T> flowGraph;

    public SourceBinding(FlowGraph<T> flowGraph, Class<T> sourceType)
    {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
    }
    public Binding<T> from(Function<Context, T> contextFunction) {
        var typedProducer = new TypedProducer<T>();
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);
        return typedProducer.with(contextFunction);
    }

    public Binding<T> from(BiFunction<Context, Class<T>, T> producerContextFunction) {
        var typedProducer = new TypedProducer<T>();
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);

        return typedProducer.with(producerContextFunction);
    }

    public Binding<T> from(Supplier<T> producerSupplier) {
        var typedProducer = new TypedProducer<T>();
        typedProducer.setType(sourceType);
        flowGraph.setProducer(typedProducer);

        return typedProducer.with(producerSupplier);
    }

    public Binding<T> from(TypedProducer<T> producer) {
        producer.setType(sourceType);
        flowGraph.setProducer(producer);
        return new Binding<>(producer.getOutputPipe(), producer);
    }

}
