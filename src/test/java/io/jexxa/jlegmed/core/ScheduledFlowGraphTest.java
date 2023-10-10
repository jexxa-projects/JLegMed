package io.jexxa.jlegmed.core;

import com.google.gson.Gson;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.dto.incoming.UpdatedContract;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.generic.producer.GenericContextProducer;
import io.jexxa.jlegmed.plugins.generic.producer.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.producer.InputStreamProducer;
import io.jexxa.jlegmed.processor.MessageCollector;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static io.jexxa.jlegmed.core.ScheduledFlowGraphTest.InputStreamURL.inputStreamOf;
import static io.jexxa.jlegmed.plugins.messaging.MessageProcessors.sendToTopicAsJSON;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScheduledFlowGraphTest {
    @Test
    void testSingleFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
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
    void testProducerURLFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var newContract = new NewContract(1);

        var inputStream = new ByteArrayInputStream(new Gson().toJson(newContract).getBytes());

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).fromURL(inputStreamOf(inputStream)).repeatUntilStopped()

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 1);
        jlegmed.stop();
    }
    @Test
    void testTransformFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
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
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector1)

                .each(20, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
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
    void testMultipleContextFlowGraphs() {
        //Arrange
        var messageCollector1 = new MessageCollector();
        var messageCollector2 = new MessageCollector();

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericContextProducer::produce)
                .andProcessWith(GenericProcessors::consoleLogger)
                .andProcessWith(messageCollector1)

                .each(20, MILLISECONDS)
                .receive(NewContract.class).from(GenericContextProducer::produce)
                .andProcessWith(GenericProcessors::consoleLogger)
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
                .receive(NewContract.class).from(GenericProducer.class)
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

    public static class MyTransformer  {
        public static Message transformToUpdatedContract(Message message) {
            return new Message(new UpdatedContract(message.getData(NewContract.class).contractNumber(), "newInfo"));
        }

        public static Message contextTransformer(Message message, Context context) {
            return new Message(new UpdatedContract(message.getData(NewContract.class).contractNumber(), "porpertiesTransformer"));
        }
    }

    public static class InputStreamURL extends ProducerURL {

        private final InputStream inputStream;
        private InputStreamProducer inputStreamProducer;

        public InputStreamURL(InputStream inputStreamReader)
        {
            this.inputStream = inputStreamReader;
        }

        @Override
        public InputStreamProducer getProducer() {
            if (inputStreamProducer == null )
            {
                this.inputStreamProducer = new InputStreamProducer(inputStream);
            }
            return inputStreamProducer;
        }

        public JLegMed repeatUntilStopped() {

            return getApplication();
        }

        public static InputStreamURL inputStreamOf(InputStream inputStream) {
            return new InputStreamURL(inputStream);
        }
    }

    @Test
    void testJMSFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith(  ScheduledFlowGraphTest::testTopicSender)
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    public static Message testTopicSender(Message message, Context context)
    {
        return sendToTopicAsJSON (message, context, "TestTopic");
    }

}
