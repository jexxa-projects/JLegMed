package io.jexxa.jlegmed.core;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TypedProcessor<U, V> implements ContextProcessor {
    private BiFunction<U, Context,V> contextFunction;
    private Function<U, V> processFunction;
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

}
