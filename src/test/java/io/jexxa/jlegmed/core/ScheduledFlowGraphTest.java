package io.jexxa.jlegmed.core;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.dto.incoming.UpdatedContract;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static io.jexxa.jlegmed.plugins.generic.producer.InputStreamURL.inputStreamOf;
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
                .receive(String.class).generatedWith(() -> "Hello World")

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
    void testFlowGraphContextSource() {
        //Arrange
        var messageCollector = new MessageCollector<String>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)

                .receive(String.class).generatedWith((context) -> "Hello World")

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
    void testFlowGraphIncrementer() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("Incrementer")
                .each(10, MILLISECONDS)

                .receive(Integer.class).generatedWith( () -> 1)

                .andProcessWith( GenericProcessors::incrementer )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
    @Test
    void testFlowGraphContextTypedSource() {
        //Arrange
        var messageCollector = new MessageCollector<String>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("TypedSource")
                .each(10, MILLISECONDS)

                .receive(String.class).generatedWith((context, type) -> "Hello World")

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
    void testProducerWithContext() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("ProducerWithContext")
                .each(10, MILLISECONDS)

                .receive(Integer.class).generatedWith(GenericProducer::counter)

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
    void testDuplicateMessage() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("DuplicateMessage")
                .each(10, MILLISECONDS)

                .receive(Integer.class).generatedWith(GenericProducer::counter)

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::duplicate)
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
        jlegmed.stop();
    }

    @Test
    void testProducerURL() {
        //Arrange
        var messageCollector = new MessageCollector<NewContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("ProducerURL")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStreamOf(inputStream)).untilStopped()

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() > 1);
        jlegmed.stop();
    }

    @Test
    void testProducerURLOnlyOnce() {
        //Arrange
        var messageCollector = new MessageCollector<NewContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("ProducerURLOnlyOnce")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStreamOf(inputStream)).onlyOnce()

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 1);
        jlegmed.stop();
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }
    @Test
    void testTransformDataWithContext() {
        //Arrange
        var messageCollector = new MessageCollector<UpdatedContract>();
        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("transformDataWithContext")
                .each(10, MILLISECONDS)
                .receive(NewContract.class).generatedWith(GenericProducer::newContract)
                .andProcessWith( ContractTransformer::contextTransformer)
                .andProcessWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<Integer>();

        var jlegmed = new JLegMed(ScheduledFlowGraphTest.class);
        jlegmed.newFlowGraph("flowGraph1")
                .each(10, MILLISECONDS)
                .receive(Integer.class).generatedWith(GenericProducer::counter)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector1::collect);

        jlegmed.newFlowGraph("flowGraph2")
                .each(20, MILLISECONDS)
                .receive(Integer.class).generatedWith(GenericProducer::counter)
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

                .receive(NewContract.class).generatedWith(GenericProducer::newContract)

                .andProcessWith(ContractTransformer::transformToUpdatedContract)
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


    private static class ContractTransformer {
        public static UpdatedContract transformToUpdatedContract(NewContract newContract) {
            return new UpdatedContract(newContract.contractNumber(), "newInfo");
        }

        public static UpdatedContract contextTransformer(NewContract newContract, Context context) {
            return new UpdatedContract(newContract.contractNumber(), "propertiesTransformer");
        }
    }
}
