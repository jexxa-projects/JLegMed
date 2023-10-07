package io.jexxa.jlegmed.core;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class JLegMed
{

    private final List<FlowGraph> flowGraphs = new ArrayList<>();
    private FlowGraph currentFlowGraph;

    @SuppressWarnings("unused")
    public <T extends Processor> JLegMed andProcessWith(Class<T> clazz)
    {
        currentFlowGraph.andProcessWith(clazz);
        return this;
    }

    public JLegMed andProcessWith(Processor processor)
    {
        currentFlowGraph.andProcessWith(processor);
        return this;
    }
    public JLegMed andProcessWith(ContextProcessor processor)
    {
        currentFlowGraph.andProcessWith(processor);
        return this;
    }

    @SuppressWarnings("java:S1172")
    public <T> ActiveFlowGraph await(Class<T> inputData) {
        var flowGraph = new ActiveFlowGraph(this);
        this.currentFlowGraph = flowGraph;
        flowGraphs.add(flowGraph);
        return flowGraph;
    }

    public void start()
    {
        flowGraphs.forEach(FlowGraph::start);
    }

    public void stop()
    {
        flowGraphs.forEach(FlowGraph::stop);
    }


    ScheduledFlowGraph each(int fixedRate, TimeUnit timeUnit)
    {
        var eachFlowgraph = new ScheduledFlowGraph(this, fixedRate, timeUnit);
        this.currentFlowGraph = eachFlowgraph;
        flowGraphs.add(eachFlowgraph);
        return eachFlowgraph;
    }


}
