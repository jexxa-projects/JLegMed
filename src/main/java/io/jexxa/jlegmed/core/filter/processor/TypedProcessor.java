package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.FilterConfig;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class TypedProcessor<T, R> implements Processor<T> {
    private final FilterConfig filterConfig = new FilterConfig();

    private final InputPipe<T> inputPipe = new InputPipe<>(this);
    private final OutputPipe<R> outputPipe = new OutputPipe<>();

    public InputPipe<T> getInputPipe()
    {
        return inputPipe;
    }

    public OutputPipe<R> getOutputPipe()
    {
        return outputPipe;
    }

    @Override
    public void process(T content, Context context) {

        do {
            filterConfig.decreaseProcessCounter();
            context.setFilterConfig(filterConfig);

            Optional.ofNullable(doProcess(content, context))
                    .ifPresent(result -> getOutputPipe().forward(result, context));

            filterConfig.resetRepeatActive();

        } while (filterConfig.isProcessedAgain());
    }

    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
    }

    protected abstract R doProcess(T content, Context context);

    public static  <T, R> TypedProcessor<T, R> processor(BiFunction<T, Context, R> processFunction)
    {
        return new TypedProcessor<>() {
            @Override
            protected R doProcess(T data, Context context) {
                return processFunction.apply(data, context);
            }
        };
    }

    public static  <T, R> TypedProcessor<T, R> processor(Function<T, R> processFunction)
    {
        return new TypedProcessor<>() {
            @Override
            protected R doProcess(T data, Context context) {
                return processFunction.apply(data);
            }
        };
    }
}
