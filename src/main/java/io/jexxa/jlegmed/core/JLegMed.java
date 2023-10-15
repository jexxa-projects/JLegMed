package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.Processor;
import io.jexxa.jlegmed.core.flowgraph.TypedProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class JLegMed
{

    private final Map<String, FlowGraph> flowGraphs = new HashMap<>();
    private FlowGraph currentFlowGraph;
    private Processor currentProcessor;

    private String currentFlowGraphID;

    private final Class<?> application;
    private final Properties properties;

    public JLegMed()
    {
        this(JLegMed.class);
    }

    public JLegMed(Class<?> application)
    {
        this.application = application;
        this.properties  = System.getProperties();
    }

    public JLegMed newFlowGraph(String flowGraphID)
    {
        this.currentFlowGraphID = flowGraphID;
        return this;
    }

    public <U, V> JLegMed andProcessWith(BiFunction<U, Context, V> processor)
    {
        this.currentProcessor = new TypedProcessor<>(processor);
        currentFlowGraph.andProcessWith(currentProcessor);
        return this;
    }

    public <U, V> JLegMed andProcessWith(Function<U,V> function)
    {
        this.currentProcessor = new TypedProcessor<>(function);
        currentFlowGraph.andProcessWith(currentProcessor);
        return this;
    }

    public  <T> JLegMed useConfig(T configuration)
    {
        this.currentProcessor.setConfiguration(configuration);
        return this;
    }

    @SuppressWarnings("java:S1172")
    public <T> ActiveFlowGraph await(Class<T> inputData) {
        if (currentFlowGraphID == null || currentFlowGraphID.isEmpty())
        {
            throw new InvalidFlowGraphException("No flowgraph id defined");
        }

        if (flowGraphs.containsKey(currentFlowGraphID))
        {
            throw new InvalidFlowGraphException("Flowgraph with ID " + currentFlowGraphID + " is already defined");
        }
        var flowGraph = new ActiveFlowGraph(this);
        this.currentFlowGraph = flowGraph;
        flowGraphs.put(currentFlowGraphID, flowGraph);
        return flowGraph;
    }

    public void start()
    {
        flowGraphs.forEach((key, value) -> value.start());
    }

    public void stop()
    {
        flowGraphs.forEach((key, value) -> value.stop());
    }


    public ScheduledFlowGraph each(int fixedRate, TimeUnit timeUnit)
    {
        if (currentFlowGraphID == null || currentFlowGraphID.isEmpty())
        {
            throw new InvalidFlowGraphException("No flowgraph id defined");
        }

        if (flowGraphs.containsKey(currentFlowGraphID))
        {
            throw new InvalidFlowGraphException("Flowgraph with ID " + currentFlowGraphID + " is already defined");
        }
        var eachFlowgraph = new ScheduledFlowGraph(this, fixedRate, timeUnit);
        this.currentFlowGraph = eachFlowgraph;
        flowGraphs.put(currentFlowGraphID, eachFlowgraph);
        return eachFlowgraph;
    }

    public static class InvalidFlowGraphException extends RuntimeException {
        InvalidFlowGraphException(String message)
        {
            super(message);
        }
    }

    public Properties getProperties()
    {
        return properties;
    }

    public String getFlowgraphID(FlowGraph flowGraph)
    {
        return flowGraphs.entrySet().stream()
                .filter( ( entry -> entry.getValue().equals(flowGraph) ))
                .map(Map.Entry::getKey)
                .findFirst().orElse("Unknown FlowGraph");
    }

}
