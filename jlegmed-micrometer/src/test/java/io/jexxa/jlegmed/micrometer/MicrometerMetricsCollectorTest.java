package io.jexxa.jlegmed.micrometer;


import io.jexxa.jlegmed.core.JLegMed;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.micrometer.MicrometerProperties.JLEGMED_PROMETHEUS_ENDPOINT;
import static io.jexxa.jlegmed.micrometer.MicrometerProperties.JLEGMED_PROMETHEUS_PORT;
import static java.lang.Integer.parseInt;
import static org.awaitility.Awaitility.await;

class MicrometerMetricsCollectorTest {
    @Test
    void initFlowGraphMetrics()
    {
        //Arrange
        var jlegmedProperties = new Properties();
        jlegmedProperties.setProperty(MicrometerProperties.JLEGMED_PROMETHEUS_PORT, "7070");
        jlegmedProperties.setProperty(JLEGMED_PROMETHEUS_ENDPOINT, "/metrics");

        var jlegmed = new JLegMed(MicrometerMetricsCollectorTest.class, jlegmedProperties);
        var objectUnderTest = new MicrometerMetricsCollector(jlegmed);
        jlegmed.registerService(objectUnderTest);

        jlegmed.newFlowGraph("HelloWorld").every(5, TimeUnit.MILLISECONDS)
                .receive(String.class)
                .from(() -> "Hello World")
                .and().consumeWith(_ -> {} );

        jlegmed.start();

        var url = "http://localhost:"
                + jlegmedProperties.getProperty(JLEGMED_PROMETHEUS_PORT)
                + jlegmedProperties.getProperty(JLEGMED_PROMETHEUS_ENDPOINT);

        //ACT / ASSERT
        await().atMost(Duration.ofSeconds(5)).until(() -> getProcessedMessages(url) > 0);
    }
    @AfterEach
    void afterEach()
    {
        Unirest.shutDown();
    }

    private int getProcessedMessages(String url) {
        String rawMetrics = Unirest.get(url).asString().getBody();

        if (rawMetrics == null || rawMetrics.isBlank()) {
            return 0;
        }

        System.out.println(rawMetrics);

        // Zeilenweise verarbeiten - absolut linear, kein Backtracking möglich
        return rawMetrics.lines()
                // 1. Nur die relevante Metrik-Zeile heraussuchen
                .filter(line -> line.startsWith("flowgraph_messages_total") && line.contains("result=\"success\""))
                .map(line -> {
                    try {
                        int closingBracketIndex = line.lastIndexOf('}');
                        if (closingBracketIndex == -1) return 0;

                        // Wert nach der Klammer ausschneiden und trimmen (z.B. "6820.0")
                        String valueStr = line.substring(closingBracketIndex + 1).trim();

                        // Das ".0" von Prometheus abschneiden, um es sicher in BigInteger zu wandeln
                        int dotIndex = valueStr.indexOf('.');
                        if (dotIndex != -1) {
                            valueStr = valueStr.substring(0, dotIndex);
                        }

                        return parseInt(valueStr);
                    } catch (Exception _) {
                        return 0; // Fehlerhafte Zeile ignorieren
                    }
                })
                .findFirst()
                .orElse(0);
    }

}