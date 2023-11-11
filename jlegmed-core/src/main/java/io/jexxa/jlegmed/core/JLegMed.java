package io.jexxa.jlegmed.core;


import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.builder.FlowGraphBuilder;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_BUILD_TIMESTAMP;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_NAME;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_PROPERTIES;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_REPOSITORY;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_VERSION;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_CONFIG_IMPORT;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_USER_TIMEZONE;

public final class JLegMed
{
    private boolean isRunning = false;
    private final Map<String, FlowGraph<?>> flowGraphs = new LinkedHashMap<>();
    private final Properties properties;
    private final Class<?> application;
    private final PropertiesLoader propertiesLoader;

    private boolean enableBanner = true;

    public JLegMed(Class<?> application)
    {
        this(application, new Properties());
    }

    public JLegMed(Class<?> application, Properties properties)
    {
        this.propertiesLoader = new PropertiesLoader(application);
        this.properties  = propertiesLoader.createProperties(properties);
        this.application = application;
        setExceptionHandler();
    }

    public FlowGraphBuilder newFlowGraph(String flowGraphID)
    {
        return new FlowGraphBuilder(flowGraphID, this);
    }


    public void addFlowGraph(String flowGraphID, FlowGraph<?> flowGraph)
    {
        flowGraphs.put(flowGraphID, flowGraph);
    }

    public synchronized void start()
    {
        showPreStartupBanner();

        flowGraphs.forEach((flowgraphID, flowgraph) -> flowgraph.start());

        showPostStartupBanner();
        isRunning = true;
    }

    @SuppressWarnings("unused")
    public void run()
    {
        start();
        waitForShutdown();
        stop();
    }

    public synchronized void stop()
    {
        isRunning = false;

        flowGraphs.forEach((flowgraphID, flowgraph) -> flowgraph.stop());
    }

    public JLegMed disableBanner()
    {
        enableBanner = false;
        return this;
    }

    public void monitorPipes(String flowGraphID, BeforeInterceptor interceptor)
    {
        if( !flowGraphs.containsKey(flowGraphID) )
        {
            throw new IllegalStateException("FlowGraph with ID " + flowGraphID + " does not exist");
        }

        flowGraphs.get(flowGraphID).monitorPipes(interceptor);
    }

    private synchronized void waitForShutdown()
    {
        setupSignalHandler();

        try
        {
            while ( isRunning ) {
                this.wait();
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
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

    public VersionInfo jlegmedInfo()
    {
        return JLegMedVersion.getVersion();
    }

    private void setExceptionHandler()
    {
        if (Thread.getDefaultUncaughtExceptionHandler() == null)
        {
            Thread.setDefaultUncaughtExceptionHandler(new JexxaExceptionHandler(this));
        }
    }
    private void setupSignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            getLogger(JLegMed.class).info("Shutdown signal received ...");
            this.internalShutdown();
        }));
    }

    private void showPreStartupBanner()
    {
        if(enableBanner) {
            var propertiesFiles = Arrays.toString(propertiesLoader.getPropertiesFiles().toArray());

            SLF4jLogger.getLogger(JLegMed.class).info("Start application      : {}", application.getSimpleName());
            SLF4jLogger.getLogger(JLegMed.class).info("JLegMed Info           : {}", jlegmedInfo());
            SLF4jLogger.getLogger(JLegMed.class).info("Application Info       : {}", applicationInfo());
            SLF4jLogger.getLogger(JLegMed.class).info("Used Properties Files  : {}", propertiesFiles);
        }
    }

    private void showPostStartupBanner()
    {
        if(enableBanner) {
            SLF4jLogger.getLogger(JLegMed.class).info("{} successfully started", application.getSimpleName());
        }
    }

    private synchronized void internalShutdown()
    {
        isRunning = false;
        notifyAll();
    }

    public boolean isRunning() {
        return isRunning;
    }


    record JexxaExceptionHandler(JLegMed jLegMed) implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            var errorMessage = getOutputMessage(e);
            //Show startup banner if enabled and jexxa not started
            if ( jLegMed.enableBanner &&
                    !jLegMed.isRunning)
            {
                jLegMed.showPreStartupBanner();
            }

            getLogger(JLegMed.class).error("Could not startup JLegMed! {}", errorMessage);
            getLogger(JLegMed.class).debug("Stack Trace: ", e);

            jLegMed.stop();
        }

        String getOutputMessage(Throwable e) {
            var stringBuilder = new StringBuilder();
            var jLegMedMessage = e.getMessage();

            Throwable rootCause = e;
            Throwable rootCauseWithMessage = null;
            if (rootCause.getMessage() != null) {
                rootCauseWithMessage = rootCause;
            }

            while (rootCause.getCause() != null && !rootCause.getCause().equals(rootCause)) {
                rootCause = rootCause.getCause();

                if (rootCause.getMessage() != null && !rootCause.getMessage().isEmpty()) {
                    rootCauseWithMessage = rootCause;
                }
            }

            var detailedMessage = ""; // Create a potential reason in from of "lastMessage -> lastException" or just "lastMessage"
            if (rootCauseWithMessage != null && !rootCauseWithMessage.equals(rootCause)) {
                detailedMessage = rootCauseWithMessage.getClass().getSimpleName() + ": " + rootCauseWithMessage.getMessage() + " -> Exception: " + rootCause.getClass().getSimpleName();
            } else {
                detailedMessage = rootCause.getMessage();
            }

            stringBuilder.append("\n* JLegMed-Message   : ").append(jLegMedMessage);
            stringBuilder.append("\n* Detailed-Message  : ").append(detailedMessage);
            stringBuilder.append("\n* 1st trace element : ").append(rootCause.getStackTrace()[0]);

            return stringBuilder.toString();
        }
    }



    public static class PropertiesLoader {
        private final Class<?> application;
        final Properties properties = new Properties();
        private final List<String> propertiesFiles = new ArrayList<>();

        public PropertiesLoader(Class<?> application) {
            this.application = Objects.requireNonNull(application);
        }

        public Properties createProperties(Properties applicationProperties) {
            properties.clear();
            propertiesFiles.clear();

            // Handle properties in the following forder:
            // 0. Add default JEXXA_CONTEXT_MAIN
            this.properties.put(JLEGMED_APPLICATION_NAME, application.getSimpleName());

            // 1. Load properties from application.properties because they have the lowest priority
            loadPropertiesFromConfig(this.properties);
            // 2. Use System properties because they have mid-priority
            this.properties.putAll(System.getProperties());  //add/overwrite system properties
            // 3. Use given properties because they have the highest priority
            this.properties.putAll(applicationProperties);  //add/overwrite given properties
            // 4. import properties that are defined by '"io.jexxa.config.import"'
            if (this.properties.containsKey(JLEGMED_CONFIG_IMPORT)) {
                importProperties(this.properties.getProperty(JLEGMED_CONFIG_IMPORT));
            }

            //5. set system properties
            setSystemProperties(properties);

            return removeEmptyValues(properties);
        }

        private void setSystemProperties(Properties properties) {
            if (properties.containsKey(JLEGMED_USER_TIMEZONE))
            {
                System.getProperties().setProperty("user.timezone", properties.getProperty(JLEGMED_USER_TIMEZONE));
            }
        }

        public List<String> getPropertiesFiles() {
            return propertiesFiles;
        }

        private void loadPropertiesFromConfig(Properties properties) {
            try ( InputStream inputStream = PropertiesLoader.class.getResourceAsStream(JLEGMED_APPLICATION_PROPERTIES) )
            {
                if (inputStream != null) {
                    properties.load(inputStream);
                    propertiesFiles.add(JLEGMED_APPLICATION_PROPERTIES);
                } else {
                    getLogger(PropertiesLoader.class).warn("Default properties file {} not available", JLEGMED_APPLICATION_PROPERTIES);
                }
            } catch ( IOException e ) {
                getLogger(PropertiesLoader.class).warn("Default properties file {} not available", JLEGMED_APPLICATION_PROPERTIES);
            }
        }

        private Properties removeEmptyValues(Properties properties) {
            var filteredMap = properties.entrySet()
                    .stream()
                    .filter(entry -> (entry.getValue() != null && !entry.getValue().toString().isEmpty()))
                    .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));

            properties.clear();
            properties.putAll(filteredMap);
            return properties;
        }

        public void importProperties(String resource) {
            //1. try to import properties as Resource from inside the jar
            try (InputStream resourceStream = PropertiesLoader.class.getResourceAsStream(resource)) {
                if (resourceStream != null) {
                    properties.load(resourceStream);
                    propertiesFiles.add(resource);
                } else {
                    throw new FileNotFoundException(resource);
                }
            } catch (IOException e) {
                //2. tries to import properties from outside the jar
                try (FileInputStream file = new FileInputStream(resource)) {
                    properties.load(file);
                    propertiesFiles.add(resource);
                } catch (IOException f) {
                    throw new IllegalArgumentException("Properties file " + resource + " not available. Please check the filename!", f);
                }
            }
        }
    }
}
