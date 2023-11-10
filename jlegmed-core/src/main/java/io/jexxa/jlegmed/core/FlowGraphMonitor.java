package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.List;
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

    protected OutputPipe<?> producerOutputPipe()
    {
        return producerOutputPipe;
    }

    protected List<Processor<?,?>> processorList()
    {
        return flowGraph.processorList();
    }

    private void registerMonitor()
    {
       var processorList = flowGraph.processorList();

       processorList.forEach( processor -> processor.outputPipe().interceptBefore(this::intercept));
       producerOutputPipe = flowGraph.producer().outputPipe();
       producerOutputPipe.interceptBefore(this::intercept);
    }
}
