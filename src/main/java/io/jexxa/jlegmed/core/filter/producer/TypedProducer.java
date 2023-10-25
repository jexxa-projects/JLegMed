package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.FilterConfig;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TypedProducer<T> implements Producer<T> {
    private Class<T> producingType;
    private Context context;
    private final FilterConfig filterConfig = new FilterConfig();

    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public void setType(Class<T> producingType)
    {
        this.producingType = producingType;
    }

    @Override
    public void setContext(Context context)
    {
        this.context = context;
    }

    protected Class<T> getType()
    {
        return producingType;
    }

    @Override
    public OutputPipe<T> getOutputPipe()
    {
        return outputPipe;
    }


    public void produceData(Context context) {
        context.setFilterConfig(filterConfig);
        T content = doProduce(context);

        if (content != null)
        {
            outputPipe.forward(content, context);
        }
    }
    protected Context getContext()
    {
        return context;
    }

    protected FilterConfig getFilterConfig()
    {
        return filterConfig;
    }

    protected <R> Optional<R> getFilterConfig(Class<R> configType)
    {
        try {
            return Optional.ofNullable(filterConfig.getConfig(configType));
        } catch (ClassCastException e)
        {
            return Optional.empty();
        }
    }

    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
    }

    protected abstract T doProduce(Context context);

    public static <T> TypedProducer<T> producer(BiFunction<Context, Class<T>, T> function)
    {
        return new TypedProducer<>() {
            @Override
            protected T doProduce(Context context) {
                return function.apply(context, getType());
            }
        };
    }

    public static <T> TypedProducer<T> producer(Function<Context, T> function)
    {
        return new TypedProducer<>() {
            @Override
            protected T doProduce(Context context) {
                return function.apply(context);
            }
        };
    }

    public static <T> TypedProducer<T> producer(Supplier<T> function)
    {
        return new TypedProducer<>() {
            @Override
            protected T doProduce(Context context) {
                return function.get();
            }
        };
    }
}
