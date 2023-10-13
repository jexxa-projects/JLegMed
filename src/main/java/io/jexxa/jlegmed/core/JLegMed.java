package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.TypedProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class JLegMed
{

    private final List<FlowGraph> flowGraphs = new ArrayList<>();
    private FlowGraph currentFlowGraph;

    public <U, V> JLegMed andProcessWith(BiFunction<U, Context, V> processor)
    {
        currentFlowGraph.andProcessWith(new TypedProcessor<>(processor));
        return this;
    }

    public <U, V> JLegMed andProcessWith(Function<U,V> function)
    {
        currentFlowGraph.andProcessWith(new TypedProcessor<>(function));
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


    public ScheduledFlowGraph each(int fixedRate, TimeUnit timeUnit)
    {
        var eachFlowgraph = new ScheduledFlowGraph(this, fixedRate, timeUnit);
        this.currentFlowGraph = eachFlowgraph;
        flowGraphs.add(eachFlowgraph);
        return eachFlowgraph;
    }


}
