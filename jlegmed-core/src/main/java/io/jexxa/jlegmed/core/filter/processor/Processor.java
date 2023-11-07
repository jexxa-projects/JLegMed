package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Processor<T, R>  extends Filter {
    private final InputPipe<T> inputPipe = new InputPipe<>(this);
    private final OutputPipe<R> outputPipe = new OutputPipe<>();

    public InputPipe<T> inputPipe()
    {
        return inputPipe;
    }

    public OutputPipe<R> outputPipe()
    {
        return outputPipe;
    }

    public void process(T data) {

        do {
            startProcessing();

            Optional.ofNullable(doProcess(data, filterContext()))
                    .ifPresent(result -> outputPipe().forward(result));

            finishedProcessing();

        } while (processAgain());
    }

    protected abstract R doProcess(T data, FilterContext context);

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

    public static  <T> Processor<T, T> consumer(BiConsumer<T, FilterContext> processFunction)
    {
        return new Processor<>() {
            @Override
            protected T doProcess(T data, FilterContext context) {
                processFunction.accept(data, context);
                return null;
            }
        };
    }

    public static  <T> Processor<T, T> consumer(Consumer<T> processFunction)
    {
        return new Processor<>() {
            @Override
            protected T doProcess(T data, FilterContext context) {
                processFunction.accept(data);
                return null;
            }
        };
    }
}
