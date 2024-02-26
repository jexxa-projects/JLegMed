package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSListener;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.jmsQueueSender;
import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.jmsTopicSender;
import static io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer.jmsQueueReceiver;
import static io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer.jmsTopicReceiver;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessagingTestIT {
    @Test
    void testQueueMessaging() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();

        jlegmed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(jmsQueueSender("MyQueue", MessageProcessor::asJSON)).useProperties("test-jms-connection");

        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( jmsQueueReceiver("MyQueue", JMSListener::asJSON)).useProperties("test-jms-connection")
                .and().consumeWith( messageCollector::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testTopicMessaging() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();

        jlegmed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( jmsTopicSender("MyTopic", MessageProcessor::asJSON) ).useProperties("test-jms-connection");

        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( jmsTopicReceiver("MyTopic", JMSListener::asJSON) ).useProperties("test-jms-connection")
                .and().consumeWith( messageCollector::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
}

