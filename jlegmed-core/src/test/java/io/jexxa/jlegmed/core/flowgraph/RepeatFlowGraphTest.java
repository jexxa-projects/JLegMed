package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepeatFlowGraphTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(RepeatFlowGraphTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testRepeatHelloWorld() {
        //Arrange
        var flowGraphID = "RepeatHelloWorld";
        var result = new ArrayList<String>();
        var repeatCounter = 10;

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'repeat'-statement ensures that the producer is triggered 'n' times.
                //Optionally, you can define a period
                .repeat(repeatCounter).atInterval(50, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> "Hello ")
                .and().processWith( data -> data + "World")
                .and().consumeWith( data -> result.add(data) );

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We wait 3 seconds at max and expect exactly the number of messages defined by `repeatCounter`
        await().atMost(3, SECONDS).until(jlegmed::waitUntilFinished);
        assertEquals (repeatCounter, result.size());
    }

}
