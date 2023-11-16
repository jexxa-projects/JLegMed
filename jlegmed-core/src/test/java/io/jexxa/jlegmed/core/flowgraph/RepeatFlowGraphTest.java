package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class RepeatFlowGraphTest {

    @Test
    void testRepeatHelloWorld() {
        //Arrange
        var flowGraphID = "RepeatHelloWorld";
        var jlegmed = new JLegMed(FlowGraphBuilderTest.class).disableBanner();
        var result = new ArrayList<String>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'repeat'-statement ensures that the producer is triggered 'n' times.
                //Optionally, you can define an interval
                .repeat(3).atInterval(50, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> "Hello ")
                .and().processWith( data -> data + "World")
                .and().consumeWith( data -> result.add(data) );

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect exactly three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> result.size() == 3);

        jlegmed.stop();
    }

}
