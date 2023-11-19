package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;

public abstract class FunctionalProducer<T> extends PassiveProducer<T> {
    @Override
    public void produceData() {
        do {
            startProcessing();

            getInvocationHandler(this)
                    .invoke(this, () -> outputPipe().forward(doProduce()));

            finishedProcessing();

        } while (processAgain());
    }

    protected abstract T doProduce();

    public static <T> FunctionalProducer<T> producer(BiFunction<FilterContext, Class<T>, T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            public void init()
            {
                Objects.requireNonNull(producingType());
            }
            @Override
            protected T doProduce() {
                return function.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(Function<FilterContext, T> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(filterContext());
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
