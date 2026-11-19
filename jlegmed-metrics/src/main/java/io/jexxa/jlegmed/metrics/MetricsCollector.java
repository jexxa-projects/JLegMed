package io.jexxa.jlegmed.metrics;

import io.jexxa.jlegmed.core.JLegMed;

public class MetricsCollector {
    private MetricsCollector() {
        /* This utility class should not be instantiated */
    }

    public static JLegMedMetricsCollector micrometerCollector(JLegMed jLegMed) {
        return new JLegMedMetricsCollector(jLegMed);
    }
}
