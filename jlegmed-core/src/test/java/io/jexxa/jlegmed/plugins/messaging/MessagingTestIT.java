package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSListener;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageSenderPool.getMessageSender;
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
                .and().consumeWith(JMSSender::myQueue).useProperties("test-jms-connection");


        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( JMSFilter.queueReceiver("MyQueue", JMSListener::asJSON)).useProperties("test-jms-connection")
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
                .and().consumeWith( JMSSender::myTopic).useProperties("test-jms-connection");

        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( JMSFilter.topicReceiver("MyTopic", JMSListener::asJSON) ).useProperties("test-jms-connection")
                .and().consumeWith( messageCollector::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    static class JMSSender {
        public static <T> void myTopic(T data, FilterContext filterContext)
        {
            getMessageSender(filterContext)
                    .send(data)
                    .addHeader("Type", data.getClass().getSimpleName())
                    .toTopic("MyTopic")
                    .asJson();
        }

        public static <T> void myQueue(T data, FilterContext filterContext)
        {
            getMessageSender(filterContext)
                    .send(data)
                    .addHeader("Type", data.getClass().getSimpleName())
                    .toQueue("MyQueue")
                    .asJson();
        }
    }
}


