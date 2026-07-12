package io.jexxa.jlegmed.micrometer;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.micrometer.MicrometerPlugin;
import io.javalin.util.JavalinLogger;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.JLegMedService;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class MicrometerMetricsCollector implements JLegMedService  {
    private static final String JLEGMED_FLOWGRAPH_MESSAGES_TOTAL = "flowgraph.messages.total";
    private static final String FLOW_GRAPH_TAG = "flow_graph";
    private static final String RESULT_TAG = "result";
    private static final String ERROR_TYPE_TAG = "error_type";

    private final int prometheusPort;
    private final String prometheusEndpoint;
    private final JLegMed jlegMed;

    private Javalin javalin;
    private PrometheusMeterRegistry registry;

    public MicrometerMetricsCollector(JLegMed jLegMed) {
        this.jlegMed = jLegMed;
        var endpoint = jLegMed.getProperties().getProperty(MicrometerProperties.JLEGMED_PROMETHEUS_ENDPOINT, "/metrics");
        prometheusEndpoint = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        var configuredPort = jLegMed.getProperties().getProperty(MicrometerProperties.JLEGMED_PROMETHEUS_PORT, "8080");
        try {
            prometheusPort = Integer.parseInt(configuredPort);
        } catch (NumberFormatException e) {
            prometheusPort = 8080;
            SLF4jLogger.getLogger(JLegMed.class).warn(
                    "Invalid value '{}' for property '{}'. Falling back to default port {}.",
                    configuredPort,
                    MicrometerProperties.JLEGMED_PROMETHEUS_PORT,
                    prometheusPort);
        }

    }

    @Override
    public void start() {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        initMetrics(registry, jlegMed);
        JavalinLogger.startupInfo = false;
        javalin = Javalin
                .create( context -> this.initJavalin(context, registry));

        javalin.start(prometheusPort);
        SLF4jLogger.getLogger(JLegMed.class).info("JLegMed Micrometer     : http://localhost:{}{}", prometheusPort, prometheusEndpoint);
    }

    @Override
    public void stop() {
        javalin.stop();
    }

    private void initMetrics(PrometheusMeterRegistry registry, JLegMed jLegMed) {
        var flowGraphs = jLegMed.getFlowGraphs();
        flowGraphs.forEach( flowGraphId -> initFlowGraphMetrics(registry, jLegMed.getFlowGraph(flowGraphId)));
    }


    private void initFlowGraphMetrics(PrometheusMeterRegistry registry, FlowGraph flowGraph) {
        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().forwardedMessages().doubleValue())
                .description("Total processed messages")
                .tag(FLOW_GRAPH_TAG, flowGraph.flowGraphID())
                .tag(RESULT_TAG, "success")
                .tag(ERROR_TYPE_TAG, "none")
                .register(registry);

        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().handledProcessingErrors().doubleValue())
                .description("Total handled error messages")
                .tag(FLOW_GRAPH_TAG, flowGraph.flowGraphID())
                .tag(RESULT_TAG, "error")
                .tag(ERROR_TYPE_TAG, "handled")
                .register(registry);

        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().unhandledProcessingErrors().doubleValue())
                .description("Total unhandled error messages")
                .tag(FLOW_GRAPH_TAG, flowGraph.flowGraphID())
                .tag(RESULT_TAG, "error")
                .tag(ERROR_TYPE_TAG, "unhandled")
                .register(registry);

    }


    private void initJavalin(JavalinConfig config, PrometheusMeterRegistry registry)
    {
        // Plugins registrieren
        config.registerPlugin(new MicrometerPlugin(micrometerConfig -> micrometerConfig.registry = registry));
        config.routes.get(prometheusEndpoint, ctx -> {
            String acceptHeader = ctx.header("Accept");
            if (acceptHeader != null && acceptHeader.contains("application/openmetrics-text")) {
                // OpenMetrics 1.0.0 Format ausgeben
                ctx.contentType("application/openmetrics-text; version=1.0.0; charset=utf-8");
                ctx.result(registry.scrape("application/openmetrics-text"));
            } else {
                // Traditional Prometheus Text-Format 0.0.4 (Fallback)
                ctx.contentType("text/plain; version=0.0.4; charset=utf-8");
                ctx.result(registry.scrape("text/plain"));
            }
        });

    }
}
