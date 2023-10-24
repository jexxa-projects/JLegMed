package io.jexxa.jlegmed.core.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.ProcessorConnector;
import io.jexxa.jlegmed.core.flowgraph.ScheduledFlowGraph;
import io.jexxa.jlegmed.core.processor.OutputPipe;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypedProducer<T> implements Producer<T> {
    private final ScheduledFlowGraph<T> scheduledFlowGraph;
    private BiFunction<Context, Class<T>, T> producerContextFunction;
    private Supplier<T> producerSupplier;
    private Function<Context, T> contextFunction;

    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public TypedProducer(ScheduledFlowGraph<T> scheduledFlowGraph) {
        this.scheduledFlowGraph = scheduledFlowGraph;
    }

    public ProcessorConnector<T> generatedWith(Function<Context, T> contextFunction) {
        this.contextFunction = contextFunction;
        scheduledFlowGraph.generatedWith(this);
        return new ProcessorConnector<>(this.outputPipe, null);
    }


    public ProcessorConnector<T> generatedWith(BiFunction<Context, Class<T>, T> producerContextFunction) {
        this.producerContextFunction = producerContextFunction;
        scheduledFlowGraph.generatedWith(this);
        return new ProcessorConnector<>(this.outputPipe, null);
    }

    public ProcessorConnector<T> generatedWith(Supplier<T> producerSupplier) {
        this.producerSupplier = producerSupplier;
        scheduledFlowGraph.generatedWith(this);
        return new ProcessorConnector<>(this.outputPipe, null);
    }

    @Override
    public void start() {
        //No action required
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

    public <U extends ProducerURL<T>> U from(U producerURL) {
        return scheduledFlowGraph.from(producerURL);
    }

    public void produce(Class<T> clazz, Context context) {
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

}
