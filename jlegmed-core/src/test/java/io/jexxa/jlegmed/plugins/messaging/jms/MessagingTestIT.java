package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.messaging.MessageDecoder;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsQueue;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsSender;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSPool.jmsTopic;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessagingTestIT {
    @Test
    void testQueueMessaging() {
        //Arrange
        JMSPool.init();
        var messageCollector = new Stack<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();

        jlegmed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(MyQueue::sendTo).useProperties("test-jms-connection");


        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( MyQueue::receiveAsJSON ).useProperties("test-jms-connection")

                .and().consumeWith( messageCollector::push );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        jlegmed.stop();
    }

    @Test
    void testTopicMessaging() {
        //Arrange
        JMSPool.init();
        var messageCollector = new Stack<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();


        jlegmed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( MyTopic::sendTo ).useProperties("test-jms-connection");

        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( MyTopic::receiveAsJSON ).useProperties("test-jms-connection")
                .and().consumeWith( messageCollector::push );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        jlegmed.stop();
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


