package io.jexxa.jlegmed.micrometer;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.micrometer.MicrometerPlugin;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class JLegMedMicrometer {
    private static final String JLEGMED_FLOWGRAPH_MESSAGES_TOTAL = "flowgraph.messages.total";
    private final PrometheusMeterRegistry registry;

    private final Javalin javalin;
    public JLegMedMicrometer(JLegMed jLegMed) {
        var prometheusPort = Integer.parseInt(jLegMed.getProperties()
                .getProperty(MicrometerProperties.JLEGMED_PROMETHEUS_PORT, "8080"));

        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        initMonitor(registry, jLegMed);
        javalin = Javalin
                .create( context -> JLegMedMicrometer.initJavalin(context, registry))
                .start(prometheusPort);

    }

    public void stop() {
        javalin.stop();
    }

    private static void initMonitor(PrometheusMeterRegistry registry, JLegMed jLegMed) {
        var flowGraphs = jLegMed.getFlowGraphs();
        flowGraphs.forEach( flowGraphId -> initMonitor(registry, jLegMed.getFlowGraph(flowGraphId)));
    }


    private static void initMonitor(PrometheusMeterRegistry registry, FlowGraph flowGraph) {
        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().forwardedMessages().doubleValue())
                .description("Total processed messages")
                .tag("flow_graph", flowGraph.flowGraphID())
                .tag("result", "success")
                .tag("error_type", "none")
                .register(registry);

        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().handledProcessingErrors().doubleValue())
                .description("Total handled error messages")
                .tag("flow_graph", flowGraph.flowGraphID())
                .tag("result", "error")
                .tag("error_type", "handled")
                .register(registry);

        FunctionCounter.builder(JLEGMED_FLOWGRAPH_MESSAGES_TOTAL, flowGraph, value -> value.processingStats().unhandledProcessingErrors().doubleValue())
                .description("Total unhandled error messages")
                .tag("flow_graph", flowGraph.flowGraphID())
                .tag("result", "error")
                .tag("error_type", "unhandled")
                .register(registry);

    }


    private static void initJavalin(JavalinConfig config, PrometheusMeterRegistry registry)
    {
        // Plugins registrieren
        config.registerPlugin(new MicrometerPlugin(micrometerConfig -> micrometerConfig.registry = registry));
        config.routes.get("/metrics", ctx -> {
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
