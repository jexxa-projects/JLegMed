package io.jexxa.jlegmed.core;

import com.google.gson.Gson;
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
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(String.class).generatedWith(() -> "Hello World")

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
    void testFlowGraphContextSource() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)

                .receive(String.class).generatedWith((context) -> "Hello World")

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
    void testFlowGraphContextTypedSource() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)

                .receive(String.class).generatedWith((context, type) -> "Hello World")

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
    void testProducerWithContext() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)

                .receive(Integer.class).generatedWith(GenericProducer::counter)

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
    void testProducerURL() {
        //Arrange
        var messageCollector = new MessageCollector();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStreamOf(inputStream)).untilStopped()

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() > 1);
        jlegmed.stop();
    }

    @Test
    void testProducerURLOnlyOnce() {
        //Arrange
        var messageCollector = new MessageCollector();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStreamOf(inputStream)).onlyOnce()

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 1);
        jlegmed.stop();
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }
    @Test
    void testTransformFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).generatedWith(GenericProducer::newContract)
                .andProcessWith( MyTransformer::contextTransformer)
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new MessageCollector();
        var messageCollector2 = new MessageCollector();

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(Integer.class).generatedWith(GenericProducer::counter)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector1)

                .each(20, MILLISECONDS)
                .receive(Integer.class).generatedWith(GenericProducer::counter)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector2);

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
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)

                .receive(NewContract.class).generatedWith(GenericProducer::newContract)

                .andProcessWith(MyTransformer::transformToUpdatedContract)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector);
        //Act
        jlegmed.start();
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();

        //Assert
        assertFalse(messageCollector.getMessages(UpdatedContract.class).isEmpty());
    }

    private static class MyTransformer  {
        public static Content transformToUpdatedContract(Content content) {
            return new Content(new UpdatedContract(content.getData(NewContract.class).contractNumber(), "newInfo"));
        }

        public static Content contextTransformer(Content content, Context context) {
            return new Content(new UpdatedContract(content.getData(NewContract.class).contractNumber(), "propertiesTransformer"));
        }
    }
}
