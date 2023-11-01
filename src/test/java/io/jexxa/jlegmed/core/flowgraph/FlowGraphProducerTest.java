package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.dto.incoming.UpdatedContract;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FlowGraphProducerTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphProducerTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }


    @Test
    void testProducerWithContext() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("ProducerWithContext")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We expect a counting value
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(2, messageCollector.getMessages().get(1));
        assertEquals(3, messageCollector.getMessages().get(2));
    }
    @Test
    void testProducerWithContextState() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("testDuplicateProducer")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(TestFilter::duplicateProducer)

                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);

        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(1, messageCollector.getMessages().get(1));
        assertEquals(2, messageCollector.getMessages().get(2));
        assertEquals(2, messageCollector.getMessages().get(3));
    }




    @Test
    void testTransformData() {
        //Arrange
        var messageCollector = new GenericCollector<UpdatedContract>();
        jlegmed.newFlowGraph("transformData")
                .each(10, MILLISECONDS)

                .receive(NewContract.class).from(TestFilter::newContract)

                .and().processWith(TestFilter::transformToUpdatedContract)
                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertFalse(messageCollector.getMessages().isEmpty());
    }


    @Test
    void testTransformDataWithContext() {
        //Arrange
        var messageCollector = new GenericCollector<UpdatedContract>();

        jlegmed.newFlowGraph("transformDataWithContext")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(TestFilter::newContract)
                .and().processWith( TestFilter::contextTransformer)
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }



}
