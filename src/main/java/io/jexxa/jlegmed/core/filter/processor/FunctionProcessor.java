package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;

import java.util.Optional;
import java.util.function.Function;

public class FunctionProcessor<T, R> extends TypedProcessor<T, R> {
    private final Function<T, R> processFunction;
    public FunctionProcessor(Function<T, R> processFunction) {
        this.processFunction = processFunction;
    }

    @Override
    protected void doProcess(T content, Context context) {
        Optional.ofNullable(processFunction.apply(content))
                .ifPresent(r -> getOutputPipe().forward(r, context));
    }
    public static  <T, R> FunctionProcessor<T, R> processor(Function<T, R> processFunction)
    {
        return new FunctionProcessor<>(processFunction);
    }
}
