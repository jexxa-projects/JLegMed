package io.jexxa.jlegmed;


import io.jexxa.jlegmed.processor.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class JLegMed
{

    private final List<ScheduledFlowGraph> scheduledFlowGraphs = new ArrayList<>();
    private FlowGraph currentFlowGraph;
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


    public void start()
    {
        scheduledFlowGraphs.forEach(ScheduledFlowGraph::start);
    }

    public void stop()
    {
        scheduledFlowGraphs.forEach(ScheduledFlowGraph::stop);
    }


    ScheduledFlowGraph each(int fixedRate, TimeUnit timeUnit)
    {
        var eachFlowgraph = new ScheduledFlowGraph(this, fixedRate, timeUnit);
        this.currentFlowGraph = eachFlowgraph;
        scheduledFlowGraphs.add(eachFlowgraph);
        return eachFlowgraph;
    }

}
