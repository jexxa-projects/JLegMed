package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedProcessor<T, R> implements Processor {
    private BiFunction<T, Context, R> contextFunction;
    private Function<T, R> processFunction;

    private Object processorConfiguration;

    private final TypedInputPipe<T> inputPipe = new TypedInputPipe<>(this);
    private final TypedOutputPipe<R> outputPipe = new TypedOutputPipe<>();

    public TypedProcessor(BiFunction<T, Context, R> contextFunction) {
        this.contextFunction = contextFunction;
    }

    public TypedProcessor(Function<T, R> processFunction) {
        this.processFunction = processFunction;
    }

    public TypedInputPipe<T> getInputPipe()
    {
        return inputPipe;
    }

    public TypedOutputPipe<R> getOutputPipe()
    {
        return outputPipe;
    }

    @Override
    public Content process(Content content, Context context) {
        context.setProcessorConfiguration(processorConfiguration);
        if (processFunction != null) {
            var result = processFunction.apply((T) content.getData());
            if (result != null) {
                getOutputPipe().forward(new Content(result), context);
                return new Content(result);
            }
            return null;
        }

        if (contextFunction != null) {
            var result = contextFunction.apply((T) content.getData(), context);
            if (result != null) {
                getOutputPipe().forward(new Content(result), context);
                return new Content(result);
            }
            return null;
        }

        return null;
    }

    public <U> void setConfiguration(U configuration) {
        this.processorConfiguration = configuration;
    }

    public Object getConfiguration() {
        return this.processorConfiguration;
    }
}
