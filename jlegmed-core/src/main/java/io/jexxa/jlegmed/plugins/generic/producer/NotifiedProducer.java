package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

public abstract class NotifiedProducer <T,  R> extends ThreadedProducer<R> {
    private boolean isRunning = false;
    private final String name;
    private final Queue<ProcessingError<T>> inputQueue = new ArrayDeque<>();
    protected NotifiedProducer(String name) {
        this.name = name;
    }
    @Override
    public String name() {
        return name;
    }

    public synchronized ProcessingError<T> notify(ProcessingError<T> processingError)
    {
        inputQueue.add(processingError);
        this.notifyAll();
        return processingError;
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
                        SLF4jLogger.getLogger(BiFunctionMultiplexer.class).error("Inner thread was interrupted");
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

    private void forwardData(R data)
    {
        try {
            outputPipe().forward(data);
        } catch (ProcessingException e) {
            errorPipe().forward(new ProcessingError<>(data, e));
        }
    }

    protected abstract R produceData(T inputData, ProcessingException exception);

    @SuppressWarnings("java:S110")
    public static <T, R> NotifiedProducer<T, R> notifiedProducer(SerializableBiFunction<T, ProcessingException, R> function)
    {
        return new NotifiedProducer<>(methodNameFromLambda(function)) {
            @Override
            protected R produceData(T inputData, ProcessingException processingException) {
                return function.apply(inputData, processingException);
            }
        };
    }

}
