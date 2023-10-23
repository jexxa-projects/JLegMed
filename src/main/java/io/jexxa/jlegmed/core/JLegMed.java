package io.jexxa.jlegmed.core;


import io.jexxa.jlegmed.common.logger.SLF4jLogger;
import io.jexxa.jlegmed.common.properties.PropertiesLoader;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_BUILD_TIMESTAMP;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_NAME;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_REPOSITORY;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_VERSION;

public final class JLegMed
{

    private final Map<String, FlowGraph> flowGraphs = new HashMap<>();

    private final Properties properties;
    private final Class<?> application;

    public JLegMed(Class<?> application)
    {
        this(application, new Properties());
    }

    public JLegMed(Class<?> application, Properties properties)
    {
        this.properties  = new PropertiesLoader(application).createProperties(properties);
        this.application = application;
    }

    public FlowGraphBuilder newFlowGraph(String flowGraphID)
    {
        return new FlowGraphBuilder(flowGraphID, this);
    }


    void addFlowGraph(String flowGraphID, FlowGraph flowGraph)
    {
        flowGraphs.put(flowGraphID, flowGraph);
    }

    public void start()
    {
        SLF4jLogger.getLogger(JLegMed.class).info("Start application {}", application.getSimpleName());
        SLF4jLogger.getLogger(JLegMed.class).info("{}", applicationInfo());
        flowGraphs.forEach((key, value) -> value.start());
        SLF4jLogger.getLogger(JLegMed.class).info("{} successfully started", application.getSimpleName());
    }

    public void stop()
    {
        flowGraphs.forEach((key, value) -> value.stop());
    }

    public Properties getProperties()
    {
        return properties;
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
