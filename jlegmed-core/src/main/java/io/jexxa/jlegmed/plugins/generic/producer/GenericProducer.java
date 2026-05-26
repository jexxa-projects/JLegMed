package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.flowgraph.steps.PassiveSourceStep;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;

public class GenericProducer {
    public static <T> PassiveSourceStep<T> emit(T data ) {
        return PassiveSourceStep.passiveSourceStep(producer( () -> data));
    }

}
