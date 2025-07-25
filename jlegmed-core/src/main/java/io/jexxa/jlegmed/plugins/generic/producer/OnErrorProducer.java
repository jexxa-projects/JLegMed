package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.classNameFromLambda;


public abstract class OnErrorProducer<T,  R> extends ThreadedProducer<R> {
    private boolean isRunning = false;
    private final String name;
    private final Queue<ProcessingError<T>> inputQueue = new ArrayDeque<>();
    protected OnErrorProducer(String name, Class<?> classFromLambda) {
        super(classFromLambda);
        this.name = name;
    }
    @Override
    public String name() {
        return name;
    }

    public synchronized void notify(ProcessingError<T> processingError)
    {
        inputQueue.add(processingError);
        this.notifyAll();
    }

    @Override
    public void produceData() {
        while (isRunning) {
            List<R> result = new ArrayList<>();

            synchronized (this) {
                while (inputQueue.isEmpty() && isRunning) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        SLF4jLogger.getLogger(OnErrorProducer.class).error("Inner thread was interrupted");
                    }
                }

                while (!inputQueue.isEmpty() ) {
                    ProcessingError<T> element = inputQueue.remove();
                    result.add(produceData(element.originalMessage(), element.processingException()));
                }
            }

            result.forEach( this::forwardData );

            if (!isRunning) {
                return;
            }
        }
    }

    @Override
    public void start() {
        this.isRunning = true;
        super.start();
    }

    @Override
    public void stop() {
        synchronized (this) {
            this.isRunning = false;
            this.notifyAll();
            super.stop();
        }
    }

    protected abstract R produceData(T unhandledInputData, ProcessingException exception);

    @SuppressWarnings("java:S110")
    public static <T, R> OnErrorProducer<T, R> onErrorProducer(SerializableBiFunction<T, ProcessingException, R> function)
    {
        return new OnErrorProducer<>(
                filterNameFromLambda(function),
                classNameFromLambda(function))
        {
            @Override
            protected R produceData(T unhandledInputData, ProcessingException processingException) {
                return function.apply(unhandledInputData, processingException);
            }
        };
    }

}
