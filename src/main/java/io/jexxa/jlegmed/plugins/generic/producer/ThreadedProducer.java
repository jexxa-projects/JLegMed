package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.jlegmed.core.filter.producer.Producer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public abstract class ThreadedProducer<T> extends Producer<T> {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void start() {
        executorService.execute( this::produceData );
    }

    @Override
    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            getLogger(ThreadedProducer.class).warn("ThreadedProducer could not be stopped -> Force shutdown.", e);
            Thread.currentThread().interrupt();
        }
    }

    protected void forwardData(T data)
    {
        var invocationHandler = InvocationManager.getInvocationHandler(this);
        invocationHandler.invoke(this, this::internalForwardData, data);
    }

    private void internalForwardData(T data)
    {
        getOutputPipe().forward(data);
    }
    protected abstract void produceData();
}
