package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.function.Supplier;

public class SupplierProducer<T> extends TypedProducer<T> {
    private final Supplier<T> function;

    public SupplierProducer(Supplier<T> function)
    {
        this.function = function;
    }

    @Override
    protected T doProduce(Context context)
    {
        return function.get();
    }
}
