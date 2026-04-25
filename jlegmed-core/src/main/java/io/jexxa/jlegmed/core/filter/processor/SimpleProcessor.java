package io.jexxa.jlegmed.core.filter.processor;

public abstract class SimpleProcessor<T, R>  extends Processor<T, R> {
    protected SimpleProcessor(String name, Class<?> classFromLambda) {
        super(false, name, classFromLambda);
    }

}
