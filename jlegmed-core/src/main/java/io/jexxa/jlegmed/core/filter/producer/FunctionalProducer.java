package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Objects;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

public abstract class FunctionalProducer<T> extends PassiveProducer<T> {

    private final String name;
    protected FunctionalProducer(String name) {
        this.name = name;
    }

    protected FunctionalProducer(Class<T> sourceType, String name) {
        producingType(sourceType);
        this.name = name;
    }

    @Override
    public String name() {
        return name;
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

    public static <T> FunctionalProducer<T> producer(SerializableBiFunction<FilterContext, Class<T>, T> function, Class<T> producingType) {
        return new FunctionalProducer<>(producingType, methodNameFromLambda(function)) {
            @Override
            public void init() {
                Objects.requireNonNull(producingType());
            }

            @Override
            protected T doProduce() {
                return function.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(SerializableBiFunction<FilterContext, Class<T>, T> function) {
        return producer(function, null);
    }


    public static <T> FunctionalProducer<T> producer(SerializableFunction<FilterContext, T> function) {
        return new FunctionalProducer<>(methodNameFromLambda(function)) {
            @Override
            protected T doProduce() {
                return function.apply(filterContext());
            }
        };
    }


    public static <T> FunctionalProducer<T> producer(SerializableSupplier<T> function) {
        return new FunctionalProducer<>(methodNameFromLambda(function)) {
            @Override
            protected T doProduce() {
                return function.get();
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(PipedProducer<T> pipedProducer) {
        return new FunctionalProducer<>(methodNameFromLambda(pipedProducer)) {
            @Override
            protected T doProduce() {
                pipedProducer.produceData(filterContext(), outputPipe());
                return null;
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(SerializableConsumer<FilterContext> function) {
        return new FunctionalProducer<>(methodNameFromLambda(function)) {
            @Override
            protected T doProduce() {
                function.accept(filterContext());
                return null;
            }
        };
    }
}
