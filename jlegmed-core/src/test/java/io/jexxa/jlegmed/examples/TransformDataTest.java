package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
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
        jlegmed = new JLegMed(TransformDataTest.class)
                .disableStrictFailFast() // For testing purposes, we disable fail fast
                .disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testChangeDataType() {
        //Arrange
        var messageCollector = new Stack<TestFilter.UpdatedContract>();
        jlegmed.newFlowGraph("ChangeDataType")
                .every(10, MILLISECONDS)

                .receive(TestFilter.NewContract.class).from(TestFilter::newContract)

                .and().processWith(TestFilter::transformToUpdatedContract)
                .and().processWith(GenericProcessors::idProcessor)
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        assertFalse( messageCollector.isEmpty());
    }


    @Test
    void testTransformDataWithFilterState() {
        //Arrange
        var messageCollector = new Stack<TestFilter.UpdatedContract>();

        jlegmed.newFlowGraph("TransformDataWithFilterState")
                .every(10, MILLISECONDS)
                //TestFilter::newContract uses FilterContext to manage its state information
                .receive(TestFilter.NewContract.class).from(TestFilter::newContract)

                .and().processWith( TestFilter::contextTransformer).withoutProperties()
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    @Test
    void testSkipData() {
        //Arrange
        var messageCollector = new Stack<Integer>();

        jlegmed.newFlowGraph("ActiveFlowGraph")
                .await(Integer.class)
                .from(GenericProducer::scheduledCounter)

                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( TestFilter::skipEachSecondMessage ).withoutProperties()
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        assertEquals(1, messageCollector.toArray()[0]);
        assertEquals(3, messageCollector.toArray()[1]);
        assertEquals(5, messageCollector.toArray()[2]);
    }

    @Test
    void testDuplicateData() {
        //Arrange
        var messageCollector = new Stack<String>();

        jlegmed.newFlowGraph("DuplicateData")
                .repeat(2)
                .receive(String.class).from(() -> "HelloWorld")

                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( GenericProcessors::duplicate ).withoutProperties()
                .and().consumeWith( messageCollector::push );

        jlegmed.monitorPipes("DuplicateData", logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.size() == 4);
        assertEquals("HelloWorld", messageCollector.toArray()[0]);
        assertEquals("HelloWorld", messageCollector.toArray()[1]);
        assertEquals("HelloWorld", messageCollector.toArray()[2]);
        assertEquals("HelloWorld", messageCollector.toArray()[3]);
    }
}
