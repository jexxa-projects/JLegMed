package io.jexxa.jlegmed.core.filter.processor;

public abstract class ContextProcessor<T, R >  extends Processor<T,R, ContextProcessor<T, R>> {
    protected ContextProcessor(Class<?> classFromLambda) {
        super(true, classFromLambda.getSimpleName(),  classFromLambda);
    }
    @Override
    protected ContextProcessor<T, R> self() {
        return this;
    }
}
