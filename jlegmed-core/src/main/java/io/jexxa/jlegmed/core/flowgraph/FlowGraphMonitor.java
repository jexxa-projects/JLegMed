package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Objects;

import static io.jexxa.adapterapi.invocation.InvocationManager.getRootInterceptor;


public abstract class FlowGraphMonitor {

    private FlowGraph<?> flowGraph;
    private OutputPipe<?> producerOutputPipe;

    public void setFlowGraph(FlowGraph<?> flowGraph)
    {
        this.flowGraph = Objects.requireNonNull( flowGraph );
        registerMonitor();
    }

    public abstract void intercept(InvocationContext invocationContext);

    protected boolean isProducerOutputPipe(Object outputPipe)
    {
        return this.producerOutputPipe == outputPipe;
    }

    private void registerMonitor()
    {
        producerOutputPipe = flowGraph.producer().outputPipe();
        getRootInterceptor(producerOutputPipe).registerBefore(this::intercept);

        flowGraph.processorList().stream()
               .map(Processor::outputPipe)
               .forEach( element -> getRootInterceptor(element).registerBefore(this::intercept));
    }
}
