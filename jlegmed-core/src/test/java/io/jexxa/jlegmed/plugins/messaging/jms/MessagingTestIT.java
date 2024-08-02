package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.drivenadapter.messaging.jms.JMSSender;
import io.jexxa.common.drivenadapter.outbox.TransactionalOutboxSender;
import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.messaging.MessageDecoder;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Stack;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.setDefaultMessageSender;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsQueue;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsSender;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsSource;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsTopic;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @ParameterizedTest
    @ValueSource(classes = {JMSSender.class, TransactionalOutboxSender.class})
    void testQueueMessaging(Class<? extends MessageSender> clazz) {
        //Arrange
        setDefaultMessageSender(clazz);
        var messageCollector = new Stack<Integer>();

        // Send a simple counter via JMS message queue
        jLegMed.newFlowGraph("Send messages to queue")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(MyQueue::sendTo).useProperties("test-transactional-outbox-connection");


        // Receive message via queue again
        jLegMed.newFlowGraph("Receive messages from queue")
                .await(Integer.class).from( MyQueue::receiveAsJSON ).useProperties("test-transactional-outbox-connection")

                .and().consumeWith( messageCollector::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    @ParameterizedTest
    @ValueSource(classes = {JMSSender.class, TransactionalOutboxSender.class})
    void testTopicMessaging(Class<? extends MessageSender> clazz) {
        //Arrange
        setDefaultMessageSender(clazz);
        var messageCollector = new Stack<Integer>();


        // Send a simple counter via JMS topic
        jLegMed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( MyTopic::sendTo ).useProperties("test-transactional-outbox-connection");

        // Receive a message via JMS topic again
        jLegMed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( MyTopic::receiveAsJSON ).useProperties("test-transactional-outbox-connection")
                .and().consumeWith( messageCollector::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    @ParameterizedTest
    @ValueSource(classes = {JMSSender.class, TransactionalOutboxSender.class})
    void testJMSSelector(Class<? extends MessageSender> clazz) {
        //Arrange
        setDefaultMessageSender(clazz);
        var messageCollector = new Stack<Integer>();

        // Send a simple counter via JMS topic
        jLegMed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( MyTopic::sendTo ).useProperties("test-transactional-outbox-connection");

        // Receive a message via JMS topic again
        jLegMed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( MyTopic::receiveJMSWithSelector ).useProperties("test-transactional-outbox-connection")
                .and().consumeWith( messageCollector::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }

    @ParameterizedTest
    @ValueSource(classes = {JMSSender.class, TransactionalOutboxSender.class})
    void testInvalidJMSSelector(Class<? extends MessageSender> clazz) {
        //Arrange
        setDefaultMessageSender(clazz);
        var messageCollector = new Stack<Integer>();
        var waitCondition = await().atMost(1, SECONDS);

        // Send a simple counter via JMS topic
        jLegMed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( MyTopic::sendTo ).useProperties("test-transactional-outbox-connection");

        // Receive a message via JMS topic again
        jLegMed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( MyTopic::receiveJMSWithInvalidSelector ).useProperties("test-transactional-outbox-connection")
                .and().consumeWith( messageCollector::push );

        //Act
        jLegMed.start();

        //Assert
        assertThrows(ConditionTimeoutException.class, () -> waitCondition.until(() -> !messageCollector.isEmpty()));
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

        public static <T> JMSProducer<T> receiveJMSWithSelector()
        {
            return jmsSource(new JMSSource(JMSConfiguration.MessagingType.TOPIC,
                    "MyTopic", "Type='Integer'", JMSConfiguration.DurableType.NON_DURABLE, ""),
                    MessageDecoder::fromJSON);
        }
        public static <T> JMSProducer<T> receiveJMSWithInvalidSelector()
        {
            return jmsTopic("MyTopic", "Type='Invalid'", MessageDecoder::fromJSON);
        }
    }
}


