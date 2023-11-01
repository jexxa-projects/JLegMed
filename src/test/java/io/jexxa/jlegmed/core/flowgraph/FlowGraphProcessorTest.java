package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
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

class FlowGraphProcessorTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphProcessorTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testScheduledProcessing() {
        //Arrange
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }

    @Test
    void testChangeContent() {
        //Arrange
        var messageCollector = new GenericCollector<String>();
        var inputData = "Hello World";
        var expectedResult  = inputData + "-" + inputData;

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> inputData)

                .and().processWith( content -> content + "-" + content )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals(expectedResult, messageCollector.getMessages().get(0));
    }

    @Test
    void tesProcessorUsesContext() {
        //Arrange
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(String.class).from((context) -> "Hello World - " + context.getClass().getSimpleName())

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World - " + FilterContext.class.getSimpleName(), messageCollector.getMessages().get(0));
    }


    @Test
    void tesProcessorUsesTypeInformation() {
        //Arrange
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("TypedSource")

                .each(10, MILLISECONDS)
                .receive(String.class).from((context, type) -> "Hello World - Type:" + type.getSimpleName() + " - " + context.getClass().getSimpleName())

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World - Type:" + String.class.getSimpleName()+ " - " + FilterContext.class.getSimpleName(), messageCollector.getMessages().get(0));
    }


    @Test
    void testProcessorGeneratesMoreOutputDataForASingleInput() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("DuplicateMessage")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( GenericProcessors::duplicate)
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - Counter is now received twice
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);

        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(1, messageCollector.getMessages().get(1));
        assertEquals(2, messageCollector.getMessages().get(2));
        assertEquals(2, messageCollector.getMessages().get(3));
    }


    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new GenericCollector<Integer>();
        var messageCollector2 = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("flowGraph1")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector1::collect);


        jlegmed.newFlowGraph("flowGraph2")

                .each(20, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector2::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
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
