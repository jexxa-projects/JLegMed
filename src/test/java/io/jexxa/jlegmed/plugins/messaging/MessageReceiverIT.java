package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessors;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;
import static io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer.jmsJSONProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessageReceiverIT {
    @Test
    void testAsyncProcessing() {
        //Arrange
        var messageCollector1 = new GenericCollector<Integer>();
        var messageCollector2 = new GenericCollector<>();
        var jlegmed = new JLegMed(MessageReceiverIT.class).disableBanner();

        jlegmed.newFlowGraph("MessageSender")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(MessageProcessors::sendAsJSON).configureWith("test-jms-connection", topic("MyTopic"))
                .and().processWith(messageCollector1::collect);


        jlegmed.newFlowGraph("Async MessageReceiver")

                .await(Integer.class).from(jmsJSONProducer()).configureWith("test-jms-connection", topic("MyTopic"))

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector2::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
}


