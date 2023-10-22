package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Context;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedProcessor<T, R> implements Processor<T> {
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
    public void process(T content, Context context) {

        do {
            processorConfig.decreaseCall();
            context.setProcessorConfig(processorConfig);

            R result = null;

            if (processFunction != null) {
                result = processFunction.apply(content);
            } else if (contextFunction != null) {
                result = contextFunction.apply(content, context);
            }

            if (result != null) {
                getOutputPipe().forward(result, context);
            }
            processorConfig.resetRepeatActive();

        } while (processorConfig.isProcessedAgain());

    }

    public <U> void setConfiguration(U configuration) {
        this.processorConfig.setConfig(configuration);
    }

}
