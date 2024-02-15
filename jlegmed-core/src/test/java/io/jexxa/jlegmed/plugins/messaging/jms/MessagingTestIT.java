package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.messaging.MessageDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsQueue;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsSender;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsTopic;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessagingTestIT {
    private static JLegMed jLegMed;
    @BeforeEach
    void init() {
        jLegMed = new JLegMed(JMSPoolIT.class)
                .useTechnology(JMSPool.class)
                .disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void testQueueMessaging() {
        //Arrange
        var messageCollector = new Stack<Integer>();

        // Send a simple counter via JMS message queue
        jLegMed.newFlowGraph("Send messages to queue")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(MyQueue::sendTo).useProperties("test-jms-connection");


        // Receive message via queue again
        jLegMed.newFlowGraph("Receive messages from queue")
                .await(Integer.class).from( MyQueue::receiveAsJSON ).useProperties("test-jms-connection")

                .and().consumeWith( messageCollector::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }

    @Test
    void testTopicMessaging() {
        //Arrange
        var messageCollector = new Stack<Integer>();

        // Send a simple counter via JMS topic
        jLegMed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( MyTopic::sendTo ).useProperties("test-jms-connection");

        // Receive a message via JMS topic again
        jLegMed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( MyTopic::receiveAsJSON ).useProperties("test-jms-connection")
                .and().consumeWith( messageCollector::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }

    static class MyQueue {

        public static <T> void sendTo(T data, FilterContext filterContext)
        {
            jmsSender(filterContext)
                    .send(data)
                    .addHeader("Type", data.getClass().getSimpleName())
                    .toQueue("MyQueue")
                    .asJson();
        }

        public static <T> JMSProducer<T> receiveAsJSON()
        {
            return jmsQueue("MyQueue", MessageDecoder::fromJSON);
        }
    }

    static class MyTopic
    {
        public static <T> void sendTo(T data, FilterContext filterContext)
        {
            jmsSender(filterContext)
                    .send(data)
                    .addHeader("Type", data.getClass().getSimpleName())
                    .toTopic("MyTopic")
                    .asJson();
        }

        public static <T> JMSProducer<T> receiveAsJSON()
        {
            return jmsTopic("MyTopic", MessageDecoder::fromJSON);
        }
    }
}


