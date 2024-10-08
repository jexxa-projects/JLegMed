package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;


public abstract class ThreadedProducer<T> extends ActiveProducer<T> {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void start() {
        super.start();
        executorService.execute( this::produceData );
    }

    @Override
    public void stop() {
        super.stop();
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

    public abstract void produceData();

    protected void forwardData(T data)
    {
        try {
            outputPipe().forward(data);
        } catch (ProcessingException e) {
            errorPipe().forward(new ProcessingError<>(data, e));
        }catch (RuntimeException e) {
            errorPipe().forward(new ProcessingError<>(data, new ProcessingException(this, name() + " could not generate data", e)));
        }
    }
}
