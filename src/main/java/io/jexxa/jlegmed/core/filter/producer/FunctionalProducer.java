package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FunctionalProducer<T> extends Producer<T> {

    @Override
    public void start()
    {
        produceData();
    }


    public void produceData() {
        do {
            getState().decreaseProcessCounter();

            Optional.ofNullable(doProduce())
                    .ifPresent(result -> getOutputPipe().forward(result));

            getState().resetRepeatActive();

        } while (getState().isProcessedAgain());
    }

    protected abstract T doProduce();

    public static <T> FunctionalProducer<T> producer(BiFunction<FilterContext, Class<T>, T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(getFilterContext(), getType());
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(Function<FilterContext, T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(getFilterContext());
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
