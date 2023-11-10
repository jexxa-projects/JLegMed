package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logMonitor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowGraphMonitorTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphBuilderTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testMonitorFlowGraph() {
        //Arrange
        var messageCollector = new GenericCollector<String>();
        var message = "Hello World with JLegMed";

        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)

                .receive(String.class).from( () -> "Hello" )
                .and().processWith(data -> data + " World" )
                .and().processWith(data -> data + " with" )
                .and().processWith(data -> data + " JLegMed" )
                .and().consumeWith(messageCollector::collect);

        jlegmed.monitorWith("HelloWorld", logMonitor());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        assertEquals(message, messageCollector.getMessages().get(0));
        assertEquals(message, messageCollector.getMessages().get(1));
        assertEquals(message, messageCollector.getMessages().get(2));
    }
}
