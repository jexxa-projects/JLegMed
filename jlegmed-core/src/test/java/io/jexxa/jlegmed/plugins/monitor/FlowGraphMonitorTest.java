package io.jexxa.jlegmed.plugins.monitor;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logDataFlowStyle;
import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowGraphMonitorTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphMonitorTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testMonitorBindingsLogDataFlowStyle() {
        //Arrange
        var messageCollector = new Stack<String>();
        var message = "Hello World JLegMed";

        jlegmed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)

                .receive(String.class).from( () -> "Hello" )
                .and().processWith(data -> data + " World" )
                .and().processWith(data -> data + " JLegMed" )
                .and().consumeWith(messageCollector::push);

        //Act - Monitor pipes produces the following output for each iteration

        // [pool-1-thread-1] INFO LogMonitor - Iteration 1 : [Binding 0] Hello -> [Binding 1] Hello World -> [Binding 2] Hello World JLegMed -> [Binding 3] null -> finish
        // [pool-1-thread-1] INFO LogMonitor - Iteration 2 : [Binding 0] Hello -> [Binding 1] Hello World -> [Binding 2] Hello World JLegMed -> [Binding 3] null -> finish
        jlegmed.monitorPipes("HelloWorld", logDataFlowStyle());

        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);

        assertEquals(message, messageCollector.toArray()[0]);
        assertEquals(message, messageCollector.toArray()[1]);
        assertEquals(message, messageCollector.toArray()[2]);
    }

    @Test
    void testMonitorLogFunctionStyle() {
        //Arrange
        var messageCollector = new Stack<String>();
        var message = "Hello World JLegMed";

        jlegmed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)

                .receive(String.class).from( () -> "Hello" )
                .and().processWith(data -> data + " World" )
                .and().processWith(data -> data + " JLegMed" )
                .and().consumeWith(messageCollector::push);


        //Act - Monitor pipes produces the following output for each iteration
        // [pool-1-thread-1] INFO LogMonitor - Iteration 1 (FilterStyle) :  [Binding 0]  () -> Hello |  [Binding 1] Hello -> Hello World |  [Binding 2] Hello World -> Hello World JLegMed |  [Binding 3] Hello World JLegMed -> null
        // [pool-1-thread-1] INFO LogMonitor - Iteration 2 (FilterStyle) :  [Binding 0]  () -> Hello |  [Binding 1] Hello -> Hello World |  [Binding 2] Hello World -> Hello World JLegMed |  [Binding 3] Hello World JLegMed -> null
        jlegmed.monitorPipes("HelloWorld", logFunctionStyle());

        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);

        assertEquals(message, messageCollector.toArray()[0]);
        assertEquals(message, messageCollector.toArray()[1]);
        assertEquals(message, messageCollector.toArray()[2]);
    }
}
