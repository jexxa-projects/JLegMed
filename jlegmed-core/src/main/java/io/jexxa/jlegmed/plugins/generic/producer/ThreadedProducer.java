package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;


public abstract class ThreadedProducer<T> extends ActiveProducer<T> {

    private final ThreadedAdapter threadedAdapter = new ThreadedAdapter();

    @Override
    public void start() {
        super.start();
        threadedAdapter.register(this);
    }

    public abstract void produceData();

    public IDrivingAdapter drivingAdapter()
    {
        return threadedAdapter;
    }

    private static class ThreadedAdapter implements IDrivingAdapter
    {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();
        private ThreadedProducer<?> threadedProducer;
        @Override
        public void register(Object object) {
            this.threadedProducer = (ThreadedProducer<?>) object;
        }

        @Override
        public void start() {
            executorService.execute( threadedProducer::produceData );
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
    }

}
