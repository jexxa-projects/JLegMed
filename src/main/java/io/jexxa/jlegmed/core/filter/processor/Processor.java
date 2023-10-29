package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Processor<T, R>  extends Filter {
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

    public void process(T content) {

        do {
            getState().decreaseProcessCounter();

            Optional.ofNullable(doProcess(content, getFilterContext()))
                    .ifPresent(result -> getOutputPipe().forward(result));

            getState().resetRepeatActive();

        } while (getState().isProcessedAgain());
    }

    protected abstract R doProcess(T content, FilterContext context);

    public static  <T, R> Processor<T, R> processor(BiFunction<T, FilterContext, R> processFunction)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, FilterContext context) {
                return processFunction.apply(data, context);
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(Function<T, R> processFunction)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, FilterContext context) {
                return processFunction.apply(data);
            }
        };
    }
}
