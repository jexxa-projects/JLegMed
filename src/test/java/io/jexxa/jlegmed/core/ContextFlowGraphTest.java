package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.activeProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextFlowGraphTest {
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
    void testConfigureProducer() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("testConfigureProducer")

                .await(Integer.class)
                // Here we configure a producer that produces a counter in a specific interval
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS)).and()

                .processWith( processor(GenericProcessors::idProcessor )).and()
                .processWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }

    @Test
    void testFilterContext() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("testFilterContext")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( ContextFlowGraphTest::skipEachSecondMessage )
                .and().processWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(3, messageCollector.getMessages().get(1));
        assertEquals(5, messageCollector.getMessages().get(2));
    }

    @Test
    void testFilterContextTwice() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("FlowGraphTest")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                // Here we configure two processors of the sam type. Each of them uses its own FilterContext to skip each second message
                // At the end 2nd-4th messages are skipped.
                .and().processWith( ContextFlowGraphTest::skipEachSecondMessage )
                .and().processWith( ContextFlowGraphTest::skipEachSecondMessage )
                .and().processWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(5, messageCollector.getMessages().get(1));
        assertEquals(9, messageCollector.getMessages().get(2));
    }


    @Test
    void testMultipleFlowGraphsWithFilterContext() {
        //Arrange
        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("FlowGraphTest1")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                .and().processWith( ContextFlowGraphTest::skipEachSecondMessage )
                .and().processWith( messageCollector1::collect );


        jlegmed.newFlowGraph("FlowGraphTest2")

                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                .and().processWith( ContextFlowGraphTest::skipEachSecondMessage )
                .and().processWith( messageCollector2::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);

        assertEquals(1, messageCollector1.getMessages().get(0));
        assertEquals(3, messageCollector1.getMessages().get(1));
        assertEquals(5, messageCollector1.getMessages().get(2));

        assertEquals(1, messageCollector2.getMessages().get(0));
        assertEquals(3, messageCollector2.getMessages().get(1));
        assertEquals(5, messageCollector2.getMessages().get(2));
    }



    private static <T> T skipEachSecondMessage(T data, FilterContext context)
    {
        var stateID = "skipEachSecondMessage";
        int currentCounter = context.state(stateID, Integer.class).orElse(1);
        context.updateState(stateID, currentCounter+1);

        if (currentCounter % 2 == 0) {
            return null;
        }
        return data;
    }

}
