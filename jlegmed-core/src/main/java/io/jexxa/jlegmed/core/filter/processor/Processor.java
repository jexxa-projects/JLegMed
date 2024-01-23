package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

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

            outputPipe().forward(doProcess(data, filterContext(), outputPipe));

            finishedProcessing();

        } while (processAgain());
    }

    protected abstract R doProcess(T data, FilterContext context, OutputPipe<R> outputPipe);

    public static  <T, R> Processor<T, R> processor(BiFunction<T, FilterContext, R> processFunction)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, FilterContext filterContext, OutputPipe<R> outputPipe) {
                return processFunction.apply(data, filterContext);
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(PipedProcessor<T, R> pipedProcessor)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, FilterContext filterContext, OutputPipe<R> outputPipe) {
                pipedProcessor.processData(data, filterContext, outputPipe);
                return null;
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(Function<T, R> processFunction)
    {
        return new Processor<>() {
            @Override
            protected R doProcess(T data, FilterContext context, OutputPipe<R> outputPipe) {
                return processFunction.apply(data);
            }
        };
    }

    public static  <T> Processor<T, T> consumer(BiConsumer<T, FilterContext> processFunction)
    {
        return new Processor<>() {
            @Override
            protected T doProcess(T data, FilterContext context, OutputPipe<T> outputPipe) {
                processFunction.accept(data, context);
                return null;
            }
        };
    }

    public static  <T> Processor<T, T> consumer(Consumer<T> processFunction)
    {
        return new Processor<>() {
            @Override
            protected T doProcess(T data, FilterContext context, OutputPipe<T> outputPipe) {
                processFunction.accept(data);
                return null;
            }
        };
    }
}
