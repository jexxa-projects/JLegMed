package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.Optional;
import java.util.function.BiFunction;

public class BiFunctionProcessor<T, R> extends TypedProcessor<T, R> {
    private final BiFunction<T, Context,  R> processFunction;
    public BiFunctionProcessor(BiFunction<T, Context, R> processFunction) {
        this.processFunction = processFunction;
    }

    @Override
    protected void doProcess(T content, Context context) {
        Optional.ofNullable(processFunction.apply(content, context))
                .ifPresent(r -> getOutputPipe().forward(r, context));
    }

    public static  <T, R> BiFunctionProcessor<T, R> processor(BiFunction<T, Context, R> processFunction)
    {
        return new BiFunctionProcessor<>(processFunction);
    }

}
