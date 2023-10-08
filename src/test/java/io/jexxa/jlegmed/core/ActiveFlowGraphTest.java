package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.common.logger.SLF4jLogger;
import io.jexxa.jlegmed.processor.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.generic.producer.GenericActiveProducer;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.Context.idOf;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class ActiveFlowGraphTest {
    @Test
    void testSingleFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from(GenericActiveProducer.class)
                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testContextFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from(GenericActiveProducer.class)
                .andProcessWith( ActiveFlowGraphTest::skipEachSecondMessage )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testMultipleContextFlowGraph() {
        //Arrange
        var messageCollector1 = new MessageCollector();
        var messageCollector2 = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from(GenericActiveProducer.class)
                .andProcessWith( ActiveFlowGraphTest::skipEachSecondMessage )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector1 )

                .await(NewContract.class).from(GenericActiveProducer.class)
                .andProcessWith( ActiveFlowGraphTest::skipEachSecondMessage )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector2 );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
    private static Message skipEachSecondMessage(Message message, Context context)
    {
        var contextID = idOf(ActiveFlowGraphTest.class, "skipEachSecondMessage");
        var currentCounter = context.getContextData(contextID, Integer.class, 1);

        context.updateContextData(contextID, currentCounter+1);
        if (currentCounter % 2 == 0) {
            SLF4jLogger.getLogger(ActiveFlowGraphTest.class).info("Skip Message");
            return null;
        }
        return message;
    }
}
