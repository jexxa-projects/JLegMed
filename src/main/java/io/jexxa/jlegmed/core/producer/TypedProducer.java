package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;
import io.jexxa.jlegmed.core.processor.TypedOutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedProducer<T> implements Producer {
    private final ScheduledFlowGraph<T> scheduledFlowGraph;
    private BiFunction<Context, Class<T>, T> producerContextFunction;
    private Supplier<T> producerSupplier;
    private Function<Context, T> contextFunction;

    private final TypedOutputPipe<T> outputPipe = new TypedOutputPipe<>();

    public TypedProducer(ScheduledFlowGraph<T> scheduledFlowGraph) {
        this.scheduledFlowGraph = scheduledFlowGraph;
    }

    public FlowGraph generatedWith(Function<Context, T> contextFunction) {
        this.contextFunction = contextFunction;
        return scheduledFlowGraph.generatedWith(this);
    }


    public FlowGraph generatedWith(BiFunction<Context, Class<T>, T> producerContextFunction) {
        this.producerContextFunction = producerContextFunction;
        return scheduledFlowGraph.generatedWith(this);
    }

    public FlowGraph generatedWith(Supplier<T> producerSupplier) {
        this.producerSupplier = producerSupplier;
        return scheduledFlowGraph.generatedWith(this);
    }

    @Override
    public TypedOutputPipe<T> getOutputPipe()
    {
        return outputPipe;
    }

    public <U extends ProducerURL> U from(U producerURL) {
        return scheduledFlowGraph.from(producerURL);
    }

    @Override
    public void produce(Class<?> clazz, Context context) {
        Content content = null;
        if (producerContextFunction != null) {
            content = new Content(producerContextFunction.apply(context, (Class<T>) clazz));
        }

        if (contextFunction != null) {
            content = new Content(contextFunction.apply(context));
        }

        if (producerSupplier != null) {
            content = new Content(producerSupplier.get());
        }
        if (content != null)
        {
            outputPipe.forward(content, context);
        }
    }

    public ScheduledFlowGraph<T> getFlowGraph()
    {
        return scheduledFlowGraph;
    }

}
