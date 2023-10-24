package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.FilterConfig;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedProcessor<T, R> implements Processor<T> {
    private BiFunction<T, Context, R> contextFunction;
    private Function<T, R> processFunction;

    private final FilterConfig filterConfig = new FilterConfig();

    private final InputPipe<T> inputPipe = new InputPipe<>(this);
    private final OutputPipe<R> outputPipe = new OutputPipe<>();

    public TypedProcessor(BiFunction<T, Context, R> contextFunction) {
        this.contextFunction = contextFunction;
    }

    public TypedProcessor(Function<T, R> processFunction) {
        this.processFunction = processFunction;
    }

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

            R result = null;

            if (processFunction != null) {
                result = processFunction.apply(content);
            } else if (contextFunction != null) {
                result = contextFunction.apply(content, context);
            }

            if (result != null) {
                getOutputPipe().forward(result, context);
            }
            filterConfig.resetRepeatActive();

        } while (filterConfig.isProcessedAgain());

    }

    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
    }

}
