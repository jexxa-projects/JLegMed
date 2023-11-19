package io.jexxa.jlegmed.plugins.monitor;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        var messageCollector = new GenericCollector<String>();
        var message = "Hello World JLegMed";

        jlegmed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)

                .receive(String.class).from( () -> "Hello" )
                .and().processWith(data -> data + " World" )
                .and().processWith(data -> data + " JLegMed" )
                .and().consumeWith(messageCollector::collect);

        jlegmed.monitorPipes("HelloWorld", logDataFlowStyle());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        assertEquals(message, messageCollector.getMessages().get(0));
        assertEquals(message, messageCollector.getMessages().get(1));
        assertEquals(message, messageCollector.getMessages().get(2));
    }

    @Test
    void testMonitorLogFunctionStyle() {
        //Arrange
        var messageCollector = new GenericCollector<String>();
        var message = "Hello World JLegMed";

        jlegmed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)

                .receive(String.class).from( () -> "Hello" )
                .and().processWith(data -> data + " World" )
                .and().processWith(data -> data + " JLegMed" )
                .and().consumeWith(messageCollector::collect);

        jlegmed.monitorPipes("HelloWorld", logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        assertEquals(message, messageCollector.getMessages().get(0));
        assertEquals(message, messageCollector.getMessages().get(1));
        assertEquals(message, messageCollector.getMessages().get(2));
    }
}
