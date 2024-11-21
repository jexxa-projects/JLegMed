package io.jexxa.jlegmed.plugins.generic.muxer;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.plugins.generic.producer.ThreadedProducer;

import java.time.Duration;
import java.time.Instant;


public abstract class SynchronizedMultiplexer<U, V, R> extends ThreadedProducer<R> {

    private boolean isRunning = false;
    private final String name;
    private U firstData = null;
    private V secondData = null;
    private final Duration muxTimeout;

    protected SynchronizedMultiplexer(String name, Duration muxTimeout) {
        this.name = name;
        this.muxTimeout = muxTimeout;
    }

    protected abstract R multiplexData(U firstData, V secondData);

    @Override
    public String name() {
        return name;
    }

    public U firstInput(U firstData) {
        synchronized (this) {
            this.firstData = firstData;
            this.notifyAll();
            waitUntilProcessedData(muxTimeout);
        }
        return firstData;
    }

    public V secondInput(V secondData) {
        synchronized (this) {
            this.secondData = secondData;
            this.notifyAll();
            waitUntilProcessedData(muxTimeout);
        }

        return secondData;
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

    @Override
    public void produceData() {
        U tmpFirstData;
        V tmpSecondData;

        while (isRunning) {
            synchronized (this) {
                while ((this.firstData == null || this.secondData == null) && isRunning) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        SLF4jLogger.getLogger(SynchronizedMultiplexer.class).error("Inner thread was interrupted");
                    }
                }

                tmpFirstData = firstData;
                tmpSecondData = secondData;
                firstData = null;
                secondData = null;
                this.notifyAll(); // Notify about new data can be feed in
            }
            if (tmpFirstData != null && tmpSecondData != null) { // In case we stop the muxer values could be null
                forwardData(multiplexData(tmpFirstData, tmpSecondData));
            }

            if (!isRunning) {
                return;
            }
        }
    }

    private synchronized void waitUntilProcessedData(Duration timeout) throws ProcessingException {
        Instant currentTime = Instant.now();
        while ( (firstData != null || secondData != null) && isRunning) {
            try {
                this.wait(timeout.toMillis());
                if ( Duration.between(currentTime, Instant.now()).compareTo(timeout) > 0 ) {
                    firstData = null; secondData = null;
                    throw new ProcessingException(name(), "Timout occurred when multiplexing data", null);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                SLF4jLogger.getLogger(SynchronizedMultiplexer.class).error("Inner thread was interrupted");
                throw new ProcessingException(name(), "Timeout in Multiplexer occured", e.getCause());
            }
        }
    }

    @Override
    protected void forwardData(R data)
    {
        try {
            outputPipe().forward(data);
        } catch (ProcessingException e) {
            errorPipe().forward(new ProcessingError<>(data, e));
        }catch (RuntimeException e) {
            errorPipe().forward(new ProcessingError<>(data, new ProcessingException(name(), name() + " could not generate data", e)));
        }
    }

}
