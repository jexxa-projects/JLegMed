package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessors;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducerURL;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory.DestinationType.TOPIC;
import static io.jexxa.jlegmed.plugins.messaging.processor.MessageSender.Configuration.topic;
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

                .receive(Integer.class).generatedWith(GenericProducer::counter)

                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(MessageProcessors::sendAsJSON).useConfig(topic("MyTopic", "test-jms-connection"))
                .andProcessWith(messageCollector1::collect);

        jlegmed.newFlowGraph("MessageReceiver")
                .await(Integer.class)
                .from(topicURL("MyTopic", "test-jms-connection"))
                .asJSON()
                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(messageCollector2::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    static <T> JMSProducerURL<T> topicURL(String topicName, String connectionName)
    {
        return new JMSProducerURL<>(new MessageSender.Configuration(TOPIC, topicName, connectionName));
    }
}


