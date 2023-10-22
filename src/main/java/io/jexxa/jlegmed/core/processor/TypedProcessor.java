package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedProcessor<T, R> implements Processor {
    private BiFunction<T, Context, R> contextFunction;
    private Function<T, R> processFunction;

    private final ProcessorConfig processorConfig = new ProcessorConfig();

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
    public void process(Content content, Context context) {

        do {
            processorConfig.decreaseCall();
            context.setProcessorConfig(processorConfig);

            R result = null;

            if (processFunction != null) {
                result = processFunction.apply((T) content.getData());
            } else if (contextFunction != null) {
                result = contextFunction.apply((T) content.getData(), context);
            }

            if (result != null) {
                getOutputPipe().forward(new Content(result), context);
            }
            processorConfig.resetRepeatActive();

        } while (processorConfig.isProcessedAgain());

    }

    public <U> void setConfiguration(U configuration) {
        this.processorConfig.setConfig(configuration);
    }

}