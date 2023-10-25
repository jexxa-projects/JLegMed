package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.function.BiFunction;

public class BiFunctionProducer<T> extends TypedProducer<T> {
    private final BiFunction<Context, Class<T>, T> function;

    public BiFunctionProducer(BiFunction<Context, Class<T>, T> function)
    {
        this.function = function;
    }

    @Override
    protected T doProduce(Context context)
    {
        return function.apply(context, getType());
    }
}
