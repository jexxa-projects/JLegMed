package io.jexxa.jlegmed.core.filter.processor;

@SuppressWarnings("unused")
public abstract class StreamProcessor<T, R>  extends Processor<T,R> {
    protected StreamProcessor(Class<?> classFromLambda) {
        super(false, classFromLambda.getSimpleName(),  classFromLambda);
    }

    @Override
    protected R doProcess(T data) {
        doVoidProcess(data);
        return null;
    }
    protected abstract void doVoidProcess(T data);
}
