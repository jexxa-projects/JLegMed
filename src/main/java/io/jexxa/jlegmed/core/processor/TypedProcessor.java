package io.jexxa.jlegmed.core.processor;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedProcessor<U, V> implements Processor {
    private BiFunction<U, Context,V> contextFunction;
    private Function<U, V> processFunction;

    private Object processorConfiguration;
    public TypedProcessor(BiFunction<U, Context,V> contextFunction)
    {
        this.contextFunction = contextFunction;
    }

    public TypedProcessor(Function<U, V> processFunction)
    {
        this.processFunction = processFunction;
    }


    @Override
    public Content process(Content content, Context context) {
        if (processFunction != null)
        {
            var result = processFunction.apply((U) content.getData());
            if (result != null)
            {
                return new Content(result);
            }
            return null;
        }

        if (contextFunction != null)
        {
            var result = contextFunction.apply((U) content.getData(), context);
            if (result != null)
            {
                return new Content(result);
            }
            return null;
        }

        return null;
    }

    @Override
    public <T> void setConfiguration(T configuration) {
        this.processorConfiguration = configuration;
    }

    @Override
    public Object getConfiguration() {
        return this.processorConfiguration;
    }

}
