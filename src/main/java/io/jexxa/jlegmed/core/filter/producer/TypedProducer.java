package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TypedProducer<T> extends Producer<T> {

    public void produceData() {
        getContext().setFilterConfig(getFilterConfig());
        T content = doProduce();

        if (content != null)
        {
            getOutputPipe().forward(content, getContext());
        }
    }

    public void start()
    {
        produceData();
    }

    public void stop()
    {
        //No config steps required
    }

    protected abstract T doProduce();

    public static <T> TypedProducer<T> producer(BiFunction<Context, Class<T>, T> function)
    {
        return new TypedProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(getContext(), getType());
            }
        };
    }

    public static <T> TypedProducer<T> producer(Function<Context, T> function)
    {
        return new TypedProducer<>() {
            @Override
            protected T doProduce() {
                return function.apply(getContext());
            }
        };
    }

    public static <T> TypedProducer<T> producer(Supplier<T> function)
    {
        return new TypedProducer<>() {
            @Override
            protected T doProduce() {
                return function.get();
            }
        };
    }
}
