package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

public class ActiveSourceStep<T> {
    private final ActiveProducer<T> producer;
    private ActiveSourceStep(ActiveProducer<T> producer) {
        this.producer = producer;
    }

    public ActiveProducer<T> producer() {return producer;}

    public static <T> ActiveSourceStep<T> activeSourceStep(ActiveProducer<T> producer ) {
        return new ActiveSourceStep<>(producer
                .noPropertiesRequired());
    }


}
