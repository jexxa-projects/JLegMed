package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.core.filter.Binding;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedProducer<T> implements Producer<T> {
    private BiFunction<Context, Class<T>, T> producerContextFunction;
    private Supplier<T> producerSupplier;
    private Function<Context, T> contextFunction;
    private Class<T> producingType;
    private Context context;

    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public Binding<T> with(Function<Context, T> contextFunction) {
        this.contextFunction = contextFunction;
        return getConnector();
    }


    public Binding<T> with(BiFunction<Context, Class<T>, T> producerContextFunction) {
        this.producerContextFunction = producerContextFunction;
        return getConnector();
    }

    public Binding<T> with(Supplier<T> producerSupplier) {
        this.producerSupplier = producerSupplier;
        return getConnector();
    }

    public void setType(Class<T> producingType)
    {
        this.producingType = producingType;
    }
    public void setContext(Context context)
    {
        this.context = context;
    }

    protected Class<T> getType()
    {
        return producingType;
    }

    protected Binding<T> getConnector()
    {
        return new Binding<>(this.outputPipe, null);
    }

    @Override
    public void start() {
        doInit();
    }

    @Override
    public void stop() {
        //No action required
    }

    @Override
    public OutputPipe<T> getOutputPipe()
    {
        return outputPipe;
    }

    public <U extends TypedProducer<T>> U from(U producer) {
        doInit();
        return producer;
    }

    public void produceData(Class<T> clazz, Context context) {
        T content = null;
        if (producerContextFunction != null) {
            content = producerContextFunction.apply(context,clazz);
        }

        if (contextFunction != null) {
            content = contextFunction.apply(context);
        }

        if (producerSupplier != null) {
            content = producerSupplier.get();
        }
        if (content != null)
        {
            outputPipe.forward(content, context);
        }
    }
    protected Context getContext()
    {
        return context;
    }
    protected void doInit()
    {
        //Empty method to be implemented by subclasses
    }

}