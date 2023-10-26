package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FunctionalProducer<T> extends Producer<T> {
    public void start()
    {
        produceData();
    }

    public void stop()
    {
        //No config steps required
    }

    public void produceData() {
        do {
            getFilterConfig().decreaseProcessCounter();
            getContext().setFilterConfig(getFilterConfig());

            Optional.ofNullable(doProduce())
                    .ifPresent(result -> getOutputPipe().forward(result, getContext()));

            getFilterConfig().resetRepeatActive();

        } while (getFilterConfig().isProcessedAgain());
    }

    protected abstract T doProduce();

    public static <T> FunctionalProducer<T> producer(BiFunction<Context, Class<T>, T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(getContext(), getType());
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(Function<Context, T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(getContext());
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(Supplier<T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                return function.get();
            }
        };
    }
}
