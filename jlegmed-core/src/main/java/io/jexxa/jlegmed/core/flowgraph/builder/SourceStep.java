package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;

public class SourceStep<T> {
    private final PassiveProducer<T> producer;
    private SourceStep(PassiveProducer<T> producer) {
        this.producer = producer;
    }

    public PassiveProducer<T> producer() {return producer;}

    public static <T> SourceStep<T> sourceStep(SerializableSupplier<T> supplier ) {
        return new SourceStep<>(FunctionalProducer.producer(supplier)
                .noPropertiesRequired());
    }
    public static <T> SourceStep<T> sourceStep(SerializableFunction<FilterContext, T> supplier ) {
        return new SourceStep<>(FunctionalProducer.producer(supplier)
                .noPropertiesRequired());
    }
    public static <T> SourceStep<T> sourceStep(PassiveProducer<T> producer ) {
        return new SourceStep<>(producer
                .noPropertiesRequired());
    }


}
