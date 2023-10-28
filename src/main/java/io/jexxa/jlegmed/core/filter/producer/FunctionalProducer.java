package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;

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
            getContext().setFilterContext(new Context.FilterContext(getState(), getProperties(), Optional.ofNullable(getConfig())));

            Optional.ofNullable(doProduce())
                    .ifPresent(result -> getOutputPipe().forward(result, getContext()));

            getState().resetRepeatActive();

        } while (getState().isProcessedAgain());
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
