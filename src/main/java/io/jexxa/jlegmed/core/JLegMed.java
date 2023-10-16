package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.common.properties.PropertiesLoader;
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

import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_BUILD_TIMESTAMP;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_NAME;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_REPOSITORY;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_VERSION;

public final class JLegMed
{

    private final Map<String, FlowGraph> flowGraphs = new HashMap<>();
    private FlowGraph currentFlowGraph;
    private Processor currentProcessor;

    private String currentFlowGraphID;

    private final Properties properties;

    public JLegMed()
    {
        this(JLegMed.class);
    }

    public JLegMed(Class<?> application)
    {
        this(application, new Properties());
    }

    public JLegMed(Class<?> application, Properties properties)
    {
        this.properties  = new PropertiesLoader(application).createProperties(properties);
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
    public <T> ActiveFlowGraph<T> await(Class<T> inputData) {
        if (currentFlowGraphID == null || currentFlowGraphID.isEmpty())
        {
            throw new InvalidFlowGraphException("No flowgraph id defined");
        }

        if (flowGraphs.containsKey(currentFlowGraphID))
        {
            throw new InvalidFlowGraphException("Flowgraph with ID " + currentFlowGraphID + " is already defined");
        }
        var flowGraph = new ActiveFlowGraph<>(this,inputData);
        flowGraphs.put(currentFlowGraphID, flowGraph);
        this.currentFlowGraph = flowGraph;
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

    public VersionInfo getVersion()
    {
        return JLegMedVersion.getVersion();
    }

    public VersionInfo applicationInfo()
    {
        return VersionInfo.of()
                .version(properties.getProperty(JLEGMED_APPLICATION_VERSION, ""))
                .repository(properties.getProperty(JLEGMED_APPLICATION_REPOSITORY, ""))
                .buildTimestamp(properties.getProperty(JLEGMED_APPLICATION_BUILD_TIMESTAMP, ""))
                .projectName(properties.getProperty(JLEGMED_APPLICATION_NAME, ""))
                .create();
    }

}
