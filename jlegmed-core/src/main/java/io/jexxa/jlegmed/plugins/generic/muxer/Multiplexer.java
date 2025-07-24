package io.jexxa.jlegmed.plugins.generic.muxer;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;

import java.time.Duration;

import static io.jexxa.jlegmed.core.filter.Filter.filterNameFromLambda;

public class Multiplexer {

    @SuppressWarnings("java:S110") // The increased amount of inheritance is caused by anonymous implementation
    public static <U, V, R> SynchronizedMultiplexer<U, V, R> synchronizedMultiplexer(MultiplexFunction<U, V, R> multiplexFunction, Duration muxTimeout)
    {
        return new SynchronizedMultiplexer<>(filterNameFromLambda(multiplexFunction), muxTimeout) {
            @Override
            public R multiplexData(U firstData, V secondData) {
                return multiplexFunction.apply(firstData, secondData, filterContext());
            }
        };
    }

    @SuppressWarnings("java:S110") // The increased amount of inheritance is caused by anonymous implementation
    public static <U, V, R> SynchronizedMultiplexer<U, V, R> synchronizedMultiplexer(SerializableBiFunction<U, V, R> multiplexFunction, Duration muxTimeout)
    {
        return new SynchronizedMultiplexer<>(filterNameFromLambda(multiplexFunction), muxTimeout) {
            @Override
            public R multiplexData(U firstData, V secondData) {
                return multiplexFunction.apply(firstData, secondData);
            }
        };
    }
    @SuppressWarnings("java:S110") // The increased amount of inheritance is caused by anonymous implementation
    public static <U, V, R> ThreadedMultiplexer<U, V, R> threadedMultiplexer(SerializableBiFunction<U, V, R> multiplexFunction)
    {
        return new ThreadedMultiplexer<>(filterNameFromLambda(multiplexFunction)) {
            @Override
            public R multiplexData(U firstData, V secondData) {
                return multiplexFunction.apply(firstData, secondData);
            }
        };
    }

    @SuppressWarnings("java:S110") // The increased amount of inheritance is caused by anonymous implementation
    public static <U, V, R> ThreadedMultiplexer<U, V, R> threadedMultiplexer(MultiplexFunction<U, V, R> multiplexFunction)
    {
        return new ThreadedMultiplexer<>(filterNameFromLambda(multiplexFunction)) {
            @Override
            public R multiplexData(U firstData, V secondData) {
                return multiplexFunction.apply(firstData, secondData, filterContext());
            }
        };
    }

    private Multiplexer() {
        //Hide implicit public constructor
    }
}
