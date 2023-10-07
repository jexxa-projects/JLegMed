package io.jexxa.jlegmed;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;
import io.jexxa.jlegmed.processor.MessageCollector;
import io.jexxa.jlegmed.processor.StandardProcessors;
import io.jexxa.jlegmed.producer.GenericActiveProducer;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class ActiveFlowGraphTest {
    @Test
    void testSingleFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class)
                .from(GenericActiveProducer.class)
                .andProcessWith( StandardProcessors::idProcessor )
                .andProcessWith( StandardProcessors::consoleLogger )
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
}
