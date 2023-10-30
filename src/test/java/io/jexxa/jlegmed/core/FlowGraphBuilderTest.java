package io.jexxa.jlegmed.core;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.dto.incoming.UpdatedContract;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.ProducerMode.ONLY_ONCE;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.ProducerMode.UNTIL_STOPPED;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.inputStream;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FlowGraphBuilderTest {
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
    void testScheduledProcessing() {
        //Arrange
        var messageCollector = new MessageCollector<String>();

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
        var messageCollector = new MessageCollector<String>();

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( content -> content + "-" + content )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World-Hello World", messageCollector.getMessages().get(0));
    }

    @Test
    void testUseContext() {
        //Arrange
        var messageCollector = new MessageCollector<String>();

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
    void testUseTypeInformation() {
        //Arrange
        var messageCollector = new MessageCollector<String>();

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
    void testProducerWithContext() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

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
    void testProcessorGeneratesMoreOutputDataForASingleInput() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

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
    void testProducerGeneratesMoreOutputDataPerIteration() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();

        jlegmed.newFlowGraph("testDuplicateProducer")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(FlowGraphBuilderTest::duplicateProducer)

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
    void testFilterConfigUntilStopped() {
        //Arrange
        var messageCollector = new MessageCollector<NewContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        jlegmed.newFlowGraph("ProducerURL")

                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStream(inputStream)).filterConfig(UNTIL_STOPPED)

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We must receive > 1 messages
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() > 1);
    }

    @Test
    void testFilterConfigOnlyOnce() {
        //Arrange
        var messageCollector = new MessageCollector<>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        jlegmed.newFlowGraph("ProducerURLOnlyOnce")

                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStream(inputStream)).filterConfig(ONLY_ONCE)

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We receive only a single message
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() == 1);
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }

    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<Integer>();

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
        var messageCollector = new MessageCollector<UpdatedContract>();
        jlegmed.newFlowGraph("transformData")
                .each(10, MILLISECONDS)

                .receive(NewContract.class).from(GenericProducer::newContract)

                .and().processWith(FlowGraphBuilderTest::transformToUpdatedContract)
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
        var messageCollector = new MessageCollector<UpdatedContract>();

        jlegmed.newFlowGraph("transformDataWithContext")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer::newContract)
                .and().processWith( FlowGraphBuilderTest::contextTransformer)
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }


    public static UpdatedContract transformToUpdatedContract(NewContract newContract) {
        return new UpdatedContract(newContract.contractNumber(), "transformToUpdatedContract");
    }

    public static UpdatedContract contextTransformer(NewContract newContract, FilterContext context) {
        return new UpdatedContract(newContract.contractNumber(), "propertiesTransformer");
    }

    public static Integer duplicateProducer(FilterContext context) {
        var stateID = "duplicateProducer";
        var currentCounter = context.state(stateID, Integer.class).orElse(0);
        var filterState = context.processingState();

        if (!filterState.isProcessingAgain()) {
            filterState.processAgain();
            return context.updateState(stateID, currentCounter+1);
        }

        return currentCounter;
    }

}
