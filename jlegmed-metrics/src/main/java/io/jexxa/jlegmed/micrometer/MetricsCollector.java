package io.jexxa.jlegmed.micrometer;

import io.jexxa.jlegmed.core.JLegMed;

public class MetricsCollector {
    private MetricsCollector() {
        /* This utility class should not be instantiated */
    }

    public static MicrometerMetricsCollector micrometerCollector(JLegMed jLegMed) {
        return new MicrometerMetricsCollector(jLegMed);
    }
}
