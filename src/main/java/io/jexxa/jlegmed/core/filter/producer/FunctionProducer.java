package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.function.Function;

public class FunctionProducer<T> extends TypedProducer<T> {
    private final Function<Context, T> function;

    public FunctionProducer(Function<Context, T> function)
    {
        this.function = function;
    }

    @Override
    protected T doProduce(Context context)
    {
        return function.apply(context);
    }
}
