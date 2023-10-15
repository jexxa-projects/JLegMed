package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.MessageProcessors.MessageConfiguration.topic;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessagingIT {
    @Test
    void testSendMessageToTopic() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed();
        jlegmed.newFlowGraph("MessageSender")
                .each(10, MILLISECONDS)

                .receive(Integer.class).generatedWith(GenericProducer::counter)

                .andProcessWith(GenericProcessors::idProcessor)
                .andProcessWith(MessageProcessors::sendAsJSON).useConfig(topic("MyTopic", "test-jms-connection"))
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }
}
