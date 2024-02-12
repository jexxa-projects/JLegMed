package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.common.facade.logger.SLF4jLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;

public abstract class BiMultiplexer<U, V, R> extends ThreadedProducer<R> {
    private boolean isRunning = false;
    private final List<U> firstData = Collections.synchronizedList(new ArrayList<>());
    private final List<V> secondData = Collections.synchronizedList(new ArrayList<>());

    public abstract R multiplexData(U firstData, V secondData);

    protected void notifyFirstData(U firstData) {
        synchronized (this) {
            this.firstData.add(firstData);
            this.notifyAll();
        }
    }

    protected void notifySecondData(V secondData) {
        synchronized (this) {
            this.secondData.add(secondData);
            this.notifyAll();
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

    public void produceData() {

        while (isRunning) {
            U tmpFirstData;
            V tmpSecondData;

            synchronized (this) {
                while ((firstData.isEmpty() || secondData.isEmpty()) && isRunning) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        SLF4jLogger.getLogger(BiMultiplexer.class).error("Inner thread was interrupted");
                    }
                }
                if (!isRunning) {
                    return;
                }

                tmpFirstData = firstData.remove(firstData.size() - 1);
                tmpSecondData = secondData.remove(secondData.size() - 1);
            }

            getInvocationHandler(this)
                    .invoke(this, () -> outputPipe().forward(multiplexData(tmpFirstData, tmpSecondData)));
        }

    }
}
