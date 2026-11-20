package io.jexxa.jlegmed.metrics;


import io.jexxa.adapter.prometheus.MicrometerMetricsCollector;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;

public class JLegMedMetricsCollector extends MicrometerMetricsCollector {
    private static final String JLEGMED_FLOWGRAPH_MESSAGES_TOTAL = "flowgraph.messages.total";
    private static final String FLOW_GRAPH_TAG = "flow_graph";
    private static final String RESULT_TAG = "result";
    private static final String ERROR_TYPE_TAG = "error_type";

    private final JLegMed jLegMed;

    JLegMedMetricsCollector(JLegMed jLegMed) {
        super(jLegMed.getProperties());
        this.jLegMed = jLegMed;
        register(this::initMetrics);
    }

    private void initMetrics(MeterRegistry registry) {
        var flowGraphs = jLegMed.getFlowGraphs();
        flowGraphs.forEach( flowGraphId -> initFlowGraphMetrics(registry, jLegMed.getFlowGraph(flowGraphId)));
    }


    private void initFlowGraphMetrics(MeterRegistry registry, FlowGraph flowGraph) {
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

}
