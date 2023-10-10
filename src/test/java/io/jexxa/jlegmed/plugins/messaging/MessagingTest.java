package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.Context;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.Content;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.MessageProcessors.sendToTopicAsJSON;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessagingTest {

    @Test
    void testFlowGraphMessageSender() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)

                .receive(NewContract.class).generatedWith(GenericProducer::counter)

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( MessagingTest::testTopicSender)
                .andProcessWith( messageCollector );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }


    private static Content testTopicSender(Content content, Context context)
    {
        return sendToTopicAsJSON (content, context, "TestTopic");
    }
}