package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessors;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;
import static io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer.receiveJMSMessageAsJSON;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessageReceiverIT {
    @Test
    void testReceiveFromTopic() {
        //Arrange
        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<>();
        var jlegmed = new JLegMed(MessageReceiverIT.class);
        jlegmed.newFlowGraph("MessageSender")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(MessageProcessors::sendAsJSON).configureWith("test-jms-connection", topic("MyTopic"))
                .andProcessWith(messageCollector1::collect);


        jlegmed.newFlowGraph("MessageReceiver")
                .await(Integer.class).from(receiveJMSMessageAsJSON()).configureWith("test-jms-connection", topic("MyTopic"))

                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector2::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
}


