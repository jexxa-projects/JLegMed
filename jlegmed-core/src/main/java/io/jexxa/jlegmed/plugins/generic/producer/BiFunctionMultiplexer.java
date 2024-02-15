package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.facade.logger.SLF4jLogger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

public abstract class BiFunctionMultiplexer<U, V, R> extends ThreadedProducer<R> {
    private boolean isRunning = false;
    private final String name;
    private final Queue<U> firstInputQueue = new ArrayDeque<>();
    private final Queue<V> secondInputQueue = new ArrayDeque<>();

    protected BiFunctionMultiplexer(String name) {
        this.name = name;
    }

    public abstract R multiplexData(U firstData, V secondData);

    @Override
    public String name() {
        return name;
    }

    public void firstInput(U firstData) {
        synchronized (this) {
            this.firstInputQueue.add(firstData);
            this.notifyAll();
        }
    }

    public void secondInput(V secondData) {
        synchronized (this) {
            this.secondInputQueue.add(secondData);
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
            List<R> result = new ArrayList<>();

            synchronized (this) {
                while ((firstInputQueue.isEmpty() || secondInputQueue.isEmpty()) && isRunning) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        SLF4jLogger.getLogger(BiFunctionMultiplexer.class).error("Inner thread was interrupted");
                    }
                }

                //Multiplex all data in the queue
                while (!firstInputQueue.isEmpty() && !secondInputQueue.isEmpty() ) {
                    result.add(
                            multiplexData(firstInputQueue.remove(), secondInputQueue.remove())
                    );
                }
            }

            result.forEach( this::forwardData );

            if (!isRunning) {
                return;
            }
        }
    }

    private void forwardData(R data)
    {
        getInvocationHandler(this)
                .invoke(this, () -> outputPipe().forward(data));
    }

    @SuppressWarnings("java:S110") // The increased amount of inheritance is caused by anonymous implementation
    public static <U, V, R> BiFunctionMultiplexer<U, V, R> multiplexer(SerializableBiFunction<U, V, R> multiplexFunction)
    {
        return new BiFunctionMultiplexer<>(methodNameFromLambda(multiplexFunction)) {
            @Override
            public R multiplexData(U firstData, V secondData) {
                return multiplexFunction.apply(firstData, secondData);
            }
        };
    }
}
