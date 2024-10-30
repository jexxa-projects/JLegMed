package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiveFlowGraphTest {

    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(ReceiveFlowGraphTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testReceiveHelloWorld() {
        //Arrange
        var flowGraphID = "ReceiveHelloWorld";
        var result = new ArrayList<String>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'every'-statement ensures that the producer is triggered at the specified rate
                .every(500, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> "Hello ")
                .and().processWith( data -> data + "World")
                .and().consumeWith( data -> result.add(data) );

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> result.size() >= 3);
    }

    @Test
    void testReceiveDelayedHelloWorld() {
        //Arrange
        var flowGraphID = "ReceiveDelayedHelloWorld";
        var result = new ArrayList<Instant>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'every'-statement ensures that the producer is triggered at the specified rate
                .every(1, SECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(Instant.class).from(() -> Instant.now())
                .and().processWith( data -> suspend(data, Duration.of(1, ChronoUnit.SECONDS)))
                .and().consumeWith( data -> result.add(data) );

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(10, SECONDS).until(() -> result.size() >= 2);
        assertTrue(Duration.between(result.get(0), result.get(1)).compareTo(Duration.of(2, ChronoUnit.SECONDS)) >= 0);
    }

    @SuppressWarnings("java:S2925")// Awaitility does not provide a simple wait
    private static <T> T suspend(T data, Duration duration)
    {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return data;
    }



}
