package io.jexxa.jlegmed.core;


import io.jexxa.adapterapi.ConfigurationFailedException;
import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.SharedInvocationHandler;
import io.jexxa.adapterapi.invocation.TransactionalInvocationHandler;
import io.jexxa.common.drivenadapter.messaging.jms.JMSSender;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.builder.BootstrapBuilder;
import io.jexxa.jlegmed.core.flowgraph.builder.FlowGraphBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.setDefaultMessageSender;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_BUILD_TIMESTAMP;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_NAME;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_PROPERTIES;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_REPOSITORY;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_APPLICATION_VERSION;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_CONFIG_IMPORT;
import static io.jexxa.jlegmed.core.JLegMedProperties.JLEGMED_USER_TIMEZONE;
import static io.jexxa.jlegmed.core.JLegMedProperties.getAsEnvironmentVariable;
import static java.lang.System.getenv;

@SuppressWarnings("unused")
public final class JLegMed
{
    private boolean isRunning = false;
    private boolean isStopped = false;
    private final Map<String, FlowGraph> flowGraphs = new LinkedHashMap<>();
    private final Map<String, FlowGraph> bootstrapFlowGraphs = new LinkedHashMap<>();
    private final Properties properties;
    private final Class<?> application;
    private final PropertiesLoader propertiesLoader;

    private boolean enableBanner = true;
    private boolean strictFailFast = false;

    public JLegMed(Class<?> application)
    {
        this(application, new Properties());
    }

    public JLegMed(Class<?> application, Properties properties)
    {
        JexxaContext.init();
        InvocationManager.setDefaultInvocationHandler(new SharedInvocationHandler());
        setDefaultMessageSender(JMSSender.class);

        this.propertiesLoader = new PropertiesLoader(application);
        this.properties  = propertiesLoader.createProperties(properties);
        this.application = application;
        enableStrictFailFast();
        setExceptionHandler();
    }

    public FlowGraphBuilder newFlowGraph(String flowGraphID)
    {
        var flowgraphBuilder = new FlowGraphBuilder(flowGraphID, getProperties(), strictFailFast());
        addFlowGraph(flowgraphBuilder.getFlowGraph());
        return flowgraphBuilder;
    }

    public BootstrapBuilder bootstrapFlowGraph(String flowGraphID)
    {
        var bootstrapBuilder = new BootstrapBuilder(flowGraphID, getProperties());
        addBootstrapFlowGraph(bootstrapBuilder.getFlowGraph());
        return bootstrapBuilder;
    }


    public void addFlowGraph(FlowGraph flowGraph)
    {
        flowGraphs.put(flowGraph.flowGraphID(), flowGraph);
    }

    public FlowGraph getFlowGraph(String flowGraphID)
    {
        return flowGraphs.getOrDefault(flowGraphID, null);
    }
    public List<String> getFlowGraphs()
    {
        return flowGraphs.keySet().stream().toList();
    }

    public void addBootstrapFlowGraph(FlowGraph flowGraph)
    {
        bootstrapFlowGraphs.put(flowGraph.flowGraphID(), flowGraph);
    }

    public synchronized void start()
    {
        if (strictFailFast()) {
            try {
                filterProperties().stream().map(FilterProperties::properties).forEach(JexxaContext::validate);
            } catch (RuntimeException e)
            {
                throw new ConfigurationFailedException(e.getMessage(), e);
            }
        }

        showPreStartupBanner();

        bootstrapFlowGraphs.forEach((_, flowgraph) -> runBootstrapFlowgraph(flowgraph));

        flowGraphs.forEach((_, flowgraph) -> flowgraph.init());
        flowGraphs.forEach((_, flowgraph) -> flowgraph.start());

        showPostStartupBanner();
        isRunning = true;
    }

    public JLegMed useTechnology(Class<?>  ... classes) {
        for (Class<?> aClass : classes) {
            initClass(aClass);
        }
        return this;
    }


    public JLegMed enableTransactionalFlowgraph()
    {
        InvocationManager.setDefaultInvocationHandler(new TransactionalInvocationHandler());
        return this;
    }


    /**
     * @return JLegMed instance for fluent API
     */
    public JLegMed enableStrictFailFast() {
        this.strictFailFast = true;
        return this;
    }

    public JLegMed disableStrictFailFast() {
        this.strictFailFast = false;
        return this;
    }

    public boolean strictFailFast() {
        return strictFailFast;
    }

    Set<FilterProperties> filterProperties()
    {
        var result = new ArrayList<FilterProperties>();

        bootstrapFlowGraphs.forEach((_, flowgraph) -> result.addAll(flowgraph.filterProperties()));
        flowGraphs.forEach((_, flowgraph) -> result.addAll(flowgraph.filterProperties()));

        return new HashSet<>(result);
    }

    public boolean waitUntilFinished()
    {
        flowGraphs.forEach((flowgraphID, flowgraph) -> flowgraph.waitUntilFinished());
        return true;
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
        if (!isStopped) {
            isStopped = true;
            isRunning = false;

            flowGraphs.forEach((flowgraphID, flowgraph) -> flowgraph.stop());
            JexxaContext.cleanup();
            if (enableBanner) {
                SLF4jLogger.getLogger(JLegMed.class).info("{} successfully stopped", application.getSimpleName());
            }
        }
    }

    public JLegMed disableBanner()
    {
        enableBanner = false;
        return this;
    }

    public void monitorPipes(String flowGraphID, BeforeInterceptor interceptor)
    {
        if (flowGraphs.containsKey(flowGraphID))
        {
            flowGraphs.get(flowGraphID).monitorPipes(interceptor);
        } else if (bootstrapFlowGraphs.containsKey(flowGraphID))
        {
            bootstrapFlowGraphs.get(flowGraphID).monitorPipes(interceptor);
        } else
        {
            throw new IllegalStateException("FlowGraph with ID " + flowGraphID + " does not exist");
        }
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

    private void runBootstrapFlowgraph(FlowGraph flowgraph)
    {
        flowgraph.init();
        flowgraph.start();
        flowgraph.waitUntilFinished();
        flowgraph.stop();
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
            Thread.setDefaultUncaughtExceptionHandler(new JLegMedExceptionHandler(this));
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

    private void initClass(Class<?> clazz) {
        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationFailedException(e.getMessage(), e);
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
        stop();
    }

    public boolean isRunning() {
        return isRunning;
    }


    record JLegMedExceptionHandler(JLegMed jLegMed) implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            var errorMessage = getOutputMessage(e);
            //Show the startup banner if enabled and jexxa not started
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

            StackTraceElement firstStackTraceElement = null;
            if (rootCause.getStackTrace().length > 0)
            {
                firstStackTraceElement = rootCause.getStackTrace()[0];
            }


            stringBuilder.append("\n* JLegMed-Message   : ").append(jLegMedMessage);
            stringBuilder.append("\n* Detailed-Message  : ").append(detailedMessage);
            stringBuilder.append("\n* 1st trace element : ").append(firstStackTraceElement);

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
            // 5. Check if additional properties are defined as environment
            if (getenv(getAsEnvironmentVariable(JLEGMED_CONFIG_IMPORT)) != null && !getenv(getAsEnvironmentVariable(JLEGMED_CONFIG_IMPORT)).isEmpty() ) {
                importProperties(getenv(getAsEnvironmentVariable(JLEGMED_CONFIG_IMPORT)));
            }
            // 6. set system properties
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
            } catch ( IOException _ ) {
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
            } catch (IOException _) {
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
