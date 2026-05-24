package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;

public class PassiveSourceStep<T> {
    private final PassiveProducer<T> producer;
    private PassiveSourceStep(PassiveProducer<T> producer) {
        this.producer = producer;
    }

    public PassiveProducer<T> producer() {return producer;}

    public static <T> PassiveSourceStep<T> passiveSourceStep(SerializableSupplier<T> supplier ) {
        return new PassiveSourceStep<>(FunctionalProducer.producer(supplier)
                .noPropertiesRequired());
    }
    public static <T> PassiveSourceStep<T> passiveSourceStep(SerializableFunction<FilterContext, T> supplier ) {
        return new PassiveSourceStep<>(FunctionalProducer.producer(supplier)
                .noPropertiesRequired());
    }
    public static <T> PassiveSourceStep<T> passiveSourceStep(PassiveProducer<T> producer ) {
        return new PassiveSourceStep<>(producer
                .noPropertiesRequired());
    }


}
