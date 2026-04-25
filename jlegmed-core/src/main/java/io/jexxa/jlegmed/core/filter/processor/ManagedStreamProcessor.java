package io.jexxa.jlegmed.core.filter.processor;

public abstract class ManagedStreamProcessor<T, R>  extends Processor<T,R> {
    protected ManagedStreamProcessor(Class<?> classFromLambda) {
        super(true, classFromLambda.getSimpleName(),  classFromLambda);
    }

    @Override
    protected R doProcess(T data) {
        doVoidProcess(data);
        return null;
    }
    protected abstract void doVoidProcess(T data);
}
