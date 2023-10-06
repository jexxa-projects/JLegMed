package io.jexxa.jlegmed;


import io.jexxa.jlegmed.processor.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class JLegMed
{

    private final List<EachFlowGraph> eachFlowGraphs = new ArrayList<>();
    private final List<AwaitFlowGraph> awaitFlowGraphs = new ArrayList<>();
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
        awaitFlowGraphs.forEach(AwaitFlowGraph::start);
        eachFlowGraphs.forEach(EachFlowGraph::start);
    }

    public void stop()
    {
        awaitFlowGraphs.forEach(AwaitFlowGraph::stop);
        eachFlowGraphs.forEach(EachFlowGraph::stop);
    }


    EachFlowGraph each(int fixedRate, TimeUnit timeUnit)
    {
        var eachFlowgraph = new EachFlowGraph(this, fixedRate, timeUnit);
        this.currentFlowGraph = eachFlowgraph;
        eachFlowGraphs.add(eachFlowgraph);
        return eachFlowgraph;
    }

    <T> AwaitFlowGraph await(Class<T> expectedData)
    {
        AwaitFlowGraph awaitFlowGraph = new AwaitFlowGraph(this);
        awaitFlowGraph.receive(expectedData);
        currentFlowGraph = awaitFlowGraph;
        awaitFlowGraphs.add(awaitFlowGraph);
        return awaitFlowGraph;
    }


}
