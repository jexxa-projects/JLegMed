package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.Objects;


public abstract class FlowGraphMonitor {

    private FlowGraph<?> flowGraph;
    private OutputPipe<?> producerOutputPipe;

    public void setFlowGraph(FlowGraph<?> flowGraph)
    {
        this.flowGraph = Objects.requireNonNull( flowGraph );
        registerMonitor();
    }

    public abstract void intercept(OutputPipe<?> outputPipe, Object data);

    protected boolean isProducerOutputPipe(OutputPipe<?> outputPipe)
    {
        return this.producerOutputPipe == outputPipe;
    }

    private void registerMonitor()
    {
       var processorList = flowGraph.processorList();

       processorList.forEach( processor -> processor.outputPipe().interceptBefore(this::intercept));
       producerOutputPipe = flowGraph.producer().outputPipe();
       producerOutputPipe.interceptBefore(this::intercept);
    }
}
