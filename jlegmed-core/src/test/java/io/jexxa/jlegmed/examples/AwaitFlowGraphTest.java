package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Stack;

import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.scheduledProducer;
import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class AwaitFlowGraphTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(AwaitFlowGraphTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testAwaitHelloWorld() {
        //Arrange
        var flowGraphID = "AwaitHelloWorld";
        var result = new Stack<String>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                // Await enables the producer to decide when to start data processing
                .await(String.class)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .from( scheduledProducer(AwaitFlowGraphTest::hello).fixedRate(500, MILLISECONDS) )
                .and().processWith( AwaitFlowGraphTest::world)
                .and().consumeWith( result::push );

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect exactly three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> result.size() >= 3);

        jlegmed.stop();
    }

    public static String hello() { return "Hello " ;}

    public static String world(String input) { return input + "World" ;}

}
