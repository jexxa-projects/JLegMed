package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;

public abstract class FunctionalProducer<T> extends PassiveProducer<T> {

    protected FunctionalProducer(){
        // Default constructor for filters that do not need type information
    }

    protected FunctionalProducer(Class<T> sourceType) {
        producingType(sourceType);
    }

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

    public static <T> FunctionalProducer<T> producer(BiFunction<FilterContext, Class<T>, T> function, Class<T> producingType)
    {
        return new FunctionalProducer<>(producingType) {
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

    public static <T> FunctionalProducer<T> producer(BiFunction<FilterContext, Class<T>, T> function) {
        return producer(function, null);
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

    public static <T> FunctionalProducer<T> producer(PipedProducer<T> pipedProducer)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                pipedProducer.produceData(filterContext(), outputPipe());
                return null;
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(Consumer<FilterContext> function)
    {
        return new FunctionalProducer<>() {
            @Override
            protected T doProduce() {
                function.accept(filterContext());
                return null;
            }
        };
    }


}
