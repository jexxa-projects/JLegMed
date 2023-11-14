package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.activeProducer;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.schedule;
import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFilter;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransformDataTest {
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
    void testChangeDataType() {
        //Arrange
        var messageCollector = new GenericCollector<TestFilter.UpdatedContract>();
        jlegmed.newFlowGraph("ChangeDataType")
                .every(10, MILLISECONDS)

                .receive(TestFilter.NewContract.class).from(TestFilter::newContract)

                .and().processWith(TestFilter::transformToUpdatedContract)
                .and().processWith(GenericProcessors::idProcessor)
                .and().consumeWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertFalse( messageCollector.getMessages().isEmpty());
    }


    @Test
    void testTransformDataWithFilterState() {
        //Arrange
        var messageCollector = new GenericCollector<TestFilter.UpdatedContract>();

        jlegmed.newFlowGraph("TransformDataWithFilterState")
                .every(10, MILLISECONDS)
                //TestFilter::newContract uses FilterContext to manage its state information
                .receive(TestFilter.NewContract.class).from(TestFilter::newContract)

                .and().processWith( TestFilter::contextTransformer)
                .and().consumeWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }


    @Test
    void testSkipData() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("ActiveFlowGraph")
                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter, schedule(50, MILLISECONDS)))

                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( TestFilter::skipEachSecondMessage )
                .and().consumeWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(3, messageCollector.getMessages().get(1));
        assertEquals(5, messageCollector.getMessages().get(2));
    }

    @Test
    void testDuplicateData() {
        //Arrange
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("DuplicateData")
                .repeat(2)
                .receive(String.class).from(() -> "HelloWorld")

                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( GenericProcessors::duplicate )
                .and().consumeWith( messageCollector::collect );

        jlegmed.monitorPipes("DuplicateData", logFilter()::intercept);

        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() == 4);
        assertEquals("HelloWorld", messageCollector.getMessages().get(0));
        assertEquals("HelloWorld", messageCollector.getMessages().get(1));
        assertEquals("HelloWorld", messageCollector.getMessages().get(2));
        assertEquals("HelloWorld", messageCollector.getMessages().get(3));
    }
}
