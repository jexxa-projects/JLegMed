package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageCommands;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.messageProcessor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessageProcessorsIT {
    @Test
    void testSendMessageToTopic() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();
        var jlegmed = new JLegMed(MessageProcessorsIT.class).disableBanner();
        var messageCommands = new MessageCommands("MyTopic");

        jlegmed.newFlowGraph("MessageSender")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageProcessor( messageCommands::sendToTopicAsJSON )).useProperties("test-jms-connection")
                .and().processWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

}
