package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.messaging.MessageDecoder;
import org.junit.jupiter.api.Test;

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
        var messageCollector = new GenericCollector<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();

        jlegmed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(JMSSender::myQueue).useProperties("test-jms-connection");


        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( jmsQueue("MyQueue", MessageDecoder::fromJSON) ).useProperties("test-jms-connection")

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
        JMSPool.init();
        var messageCollector = new GenericCollector<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();


        jlegmed.newFlowGraph("MessageSender")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith( JMSSender::myTopic ).useProperties("test-jms-connection");

        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from( jmsTopic("MyTopic", MessageDecoder::fromJSON) ).useProperties("test-jms-connection")
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
            jmsSender(filterContext)
                    .send(data)
                    .addHeader("Type", data.getClass().getSimpleName())
                    .toTopic("MyTopic")
                    .asJson();
        }

        public static <T> void myQueue(T data, FilterContext filterContext)
        {
            jmsSender(filterContext)
                    .send(data)
                    .addHeader("Type", data.getClass().getSimpleName())
                    .toQueue("MyQueue")
                    .asJson();
        }
    }
}


