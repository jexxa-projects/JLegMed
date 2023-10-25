package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessors;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessageProcessorsTest {

    @Test
    void testSendMessageToTopic() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(MessageProcessorsTest.class);
        jlegmed.newFlowGraph("MessageSender")
                .each(10, MILLISECONDS)

                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(MessageProcessors::sendAsJSON).filterConfig(topic("MyTopic"))
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }


    @Test
    void testSendMessageToQueue() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(MessageProcessorsTest.class);
        jlegmed.newFlowGraph("MessageSender")
                .each(10, MILLISECONDS)

                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(MessageProcessors::sendAsJSON).filterConfig(queue("MyQueue"))
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }


}