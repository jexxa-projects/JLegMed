package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class ReceiveFlowGraphTest {

    @Test
    void testReceiveHelloWorld() {
        //Arrange
        var jlegmed = new JLegMed(FlowGraphBuilderTest.class).disableBanner();

        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("ReceiveHelloWorld")
                .every(500, MILLISECONDS)
                .receive(String.class).from(() -> "Hello ")

                .and().processWith( data -> data + "World")
                .and().processWith( messageCollector::collect )
                .and().consumeWith( data -> SLF4jLogger.getLogger("RepeatFlowGraphTest").info(data) );

        //Act
        jlegmed.start();

        //Assert - We expect exactly three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        jlegmed.start();
    }

}
