package io.jexxa.jlegmed.core;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.dto.incoming.UpdatedContract;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.generic.producer.InputStreamProducer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static io.jexxa.jlegmed.plugins.generic.producer.InputStreamProducer.inputStream;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScheduledFlowGraphTest {

    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<String>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testChangeContent() {
        //Arrange
        var messageCollector = new MessageCollector<String>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .andProcessWith( content -> content + "-" + content )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World-Hello World", messageCollector.getMessages().get(0));
        jlegmed.stop();
    }

    @Test
    void testUseContext() {
        //Arrange
        var messageCollector = new MessageCollector<String>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)

                .receive(String.class).from((context) -> "Hello World - " + context.getClass().getSimpleName())

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
        assertEquals("Hello World - " + Context.class.getSimpleName(), messageCollector.getMessages().get(0));
    }


    @Test
    void testUseTypeInformation() {
        //Arrange
        var messageCollector = new MessageCollector<String>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("TypedSource")
                .each(10, MILLISECONDS)
                .receive(String.class).from((context, type) -> "Hello World - Type:" + type.getSimpleName() + " - " + context.getClass().getSimpleName())

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World - Type:" + String.class.getSimpleName()+ " - " + Context.class.getSimpleName(), messageCollector.getMessages().get(0));
        jlegmed.stop();
    }

    @Test
    void testProducerWithContext() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("ProducerWithContext")
                .each(10, MILLISECONDS)

                .receive(Integer.class)
                .from(GenericProducer::counter)

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We expect a counting value
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();

        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(2, messageCollector.getMessages().get(1));
        assertEquals(3, messageCollector.getMessages().get(2));

    }

    @Test
    void testProcessorGeneratesMoreOutputDataForASingleInput() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("DuplicateMessage")
                .each(10, MILLISECONDS)

                .receive(Integer.class).from(GenericProducer::counter)
                .andProcessWith( GenericProcessors::duplicate)
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - Counter is now received twice
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
        jlegmed.stop();

        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(1, messageCollector.getMessages().get(1));
        assertEquals(2, messageCollector.getMessages().get(2));
        assertEquals(2, messageCollector.getMessages().get(3));
    }

    @Test
    void testProducerGeneratesMoreOutputDataPerIteration() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("testDuplicateProducer")
                .each(10, MILLISECONDS)

                .receive(Integer.class).from(ScheduledFlowGraphTest::duplicateProducer)
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
        jlegmed.stop();

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

        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("ProducerURL")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStream(inputStream)).filterConfig(InputStreamProducer.ProducerMode.UNTIL_STOPPED)

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We must receive > 1 messages
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() > 1);
        jlegmed.stop();
    }

    @Test
    void testFilterConfigOnlyOnce() {
        //Arrange
        var messageCollector = new MessageCollector<>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("ProducerURLOnlyOnce")
                .each(10, MILLISECONDS)
                .receive(NewContract.class)
                .from(inputStream(inputStream)).filterConfig(InputStreamProducer.ProducerMode.ONLY_ONCE)

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We receive only a single message
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() == 1);
        jlegmed.stop();
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }

    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<Integer>();

        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("flowGraph1")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector1::collect);

        jlegmed.newFlowGraph("flowGraph2")
                .each(20, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector2::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);

        jlegmed.stop();
    }

    @Test
    void testTransformData() {
        //Arrange
        var messageCollector = new MessageCollector<UpdatedContract>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("transformData")
                .each(10, MILLISECONDS)

                .receive(NewContract.class).from(GenericProducer::newContract)

                .andProcessWith(ScheduledFlowGraphTest::transformToUpdatedContract)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(GenericProcessors::consoleLogger)
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();

        //Assert
        assertFalse(messageCollector.getMessages().isEmpty());
    }


    @Test
    void testTransformDataWithContext() {
        //Arrange
        var messageCollector = new MessageCollector<UpdatedContract>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("transformDataWithContext")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer::newContract)
                .andProcessWith( ScheduledFlowGraphTest::contextTransformer)
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }


    public static UpdatedContract transformToUpdatedContract(NewContract newContract) {
        return new UpdatedContract(newContract.contractNumber(), "transformToUpdatedContract");
    }

    public static UpdatedContract contextTransformer(NewContract newContract, Context context) {
        return new UpdatedContract(newContract.contractNumber(), "propertiesTransformer");
    }

    public static Integer duplicateProducer(Context context) {
        var contextID = Context.contextID(ScheduledFlowGraphTest.class, "duplicateProducer");
        var currentCounter = context.get(contextID, Integer.class).orElse(0);

        if (context.isProcessingFinished()) {
            context.processAgain();
            return context.update(contextID, currentCounter+1);
        }

        return currentCounter;
    }

}
