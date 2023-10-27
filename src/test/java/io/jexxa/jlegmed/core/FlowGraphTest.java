package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.filter.Context.stateID;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledActiveProducer.activeProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class FlowGraphTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testSingleFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("FlowGraphTest")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                .andProcessWith( processor(GenericProcessors::idProcessor ))
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }

    @Test
    void testContextFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("FlowGraphTest")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                .andProcessWith( FlowGraphTest::skipEachSecondMessage )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }

    @Test
    void testMultipleContextFlowGraph() {
        //Arrange
        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("FlowGraphTest1")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                .andProcessWith( FlowGraphTest::skipEachSecondMessage )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector1::collect );


        jlegmed.newFlowGraph("FlowGraphTest2")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                .andProcessWith( FlowGraphTest::skipEachSecondMessage )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector2::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
    }



    private static <T> T skipEachSecondMessage(T data, Context context)
    {
        var stateID = stateID(FlowGraphTest.class, "skipEachSecondMessage");
        int currentCounter = context.getState(stateID, Integer.class).orElse(1);
        context.updateState(stateID, currentCounter+1);

        if (currentCounter % 2 == 0) {
            SLF4jLogger.getLogger(FlowGraphTest.class).info("Skip Message");
            return null;
        }
        return data;
    }

}
