package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.Filter;
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

    public void process(T content, Context context) {

        do {
            getState().decreaseProcessCounter();
            context.setFilterContext(new Context.FilterContext(getState(), getProperties(), getConfig()));

            Optional.ofNullable(doProcess(content, context))
                    .ifPresent(result -> getOutputPipe().forward(result, context));

            getState().resetRepeatActive();

        } while (getState().isProcessedAgain());
    }

    protected abstract R doProcess(T content, Context context);

    public static  <T, R> Processor<T, R> processor(BiFunction<T, Context, R> processFunction)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, Context context) {
                return processFunction.apply(data, context);
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(Function<T, R> processFunction)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, Context context) {
                return processFunction.apply(data);
            }
        };
    }
}
