package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.FilterConfig;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

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
            doProcess(content, context);
            filterConfig.resetRepeatActive();

        } while (filterConfig.isProcessedAgain());
    }

    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
    }

    protected abstract void doProcess(T content, Context context);

}
