package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSListener;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.sendToQueue;
import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.sendToTopic;
import static io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer.jmsQueue;
import static io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer.jmsTopic;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MessagingTestIT {
    @ParameterizedTest
    @MethodSource("provideSenderReceiver")
    void testAsyncProcessingWithTopic(Pair<Processor<Integer, Integer>,Producer<Integer> > senderReceiver) {
        //Arrange
        var messageCollector1 = new GenericCollector<Integer>();
        var messageCollector2 = new GenericCollector<Integer>();
        var jlegmed = new JLegMed(MessagingTestIT.class).disableBanner();

        jlegmed.newFlowGraph("MessageSender")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().processWith(senderReceiver.getLeft()).useProperties("test-jms-connection")
                .and().processWith(messageCollector1::collect);

        jlegmed.newFlowGraph("Async MessageReceiver")
                .await(Integer.class).from(senderReceiver.getRight()).useProperties("test-jms-connection")
                .and().processWith(messageCollector2::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    static Stream<Pair<Processor<Integer, Integer>,Producer<Integer> >> provideSenderReceiver()
    {
        return Stream.of(
                Pair.of(sendToQueue("MyQueue", MessageProcessor::asJSON), jmsQueue("MyQueue", JMSListener::asJSON)),
                Pair.of(sendToTopic("MyTopic", MessageProcessor::asJSON), jmsTopic("MyTopic", JMSListener::asJSON))
        );
    }

}


