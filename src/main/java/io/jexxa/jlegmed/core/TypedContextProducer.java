package io.jexxa.jlegmed.core;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedContextProducer<T> implements ContextProducer {
    private final ScheduledFlowGraph scheduledFlowGraph;
    private BiFunction<Context, Class<T>, T> producerContextFunction;
    private Supplier<T> producerSupplier;
    private Function<Context, T> contextFunction;
    TypedContextProducer(ScheduledFlowGraph scheduledFlowGraph)
    {
        this.scheduledFlowGraph = scheduledFlowGraph;
    }

    public JLegMed generatedWith( Function<Context, T> contextFunction)
    {
        this.contextFunction = contextFunction;
        return scheduledFlowGraph.generatedWith(this);
    }

    public JLegMed generatedWith( BiFunction<Context, Class<T>, T> producerContextFunction)
    {
        this.producerContextFunction = producerContextFunction;
        return scheduledFlowGraph.generatedWith(this);
    }

    public JLegMed generatedWith( Supplier<T> producerSupplier)
    {
        this.producerSupplier = producerSupplier;
        return scheduledFlowGraph.generatedWith(this);
    }
    public <U extends ProducerURL> U from(U producerURL) {
        return scheduledFlowGraph.from(producerURL);
    }

    @Override
    public Object produce(Class<?> clazz, Context context) {
        if (producerContextFunction != null)
        {
            return producerContextFunction.apply(context, (Class<T>) clazz);
        }

        if (contextFunction != null)
        {
            return contextFunction.apply(context);
        }

        if (producerSupplier != null)
        {
            return producerSupplier.get();
        }
        return null;
    }
}