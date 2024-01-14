package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.jmsQueueSender;
import static io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessor.jmsTopicSender;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageProcessorTest {
    @ParameterizedTest
    @MethodSource("provideMessageSender")
    void testSendAsJSON(Processor<String, String> objectUnderTest)
    {
        //Arrange
        var inputData = "Hello World!";
        var receivingPipe = new CollectingInputPipe<String>();

        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(inputData, receivingPipe.getCollectedData().get(0));
        objectUnderTest.reachDeInit();
    }

    private static Stream<Processor<String, String>> provideMessageSender()
    {
        Processor<String, String> queueSender = jmsQueueSender("MyQueue", MessageProcessor::asJSON);
        Processor<String, String> topicSender = jmsTopicSender("MyTopic", MessageProcessor::asJSON);
        return Stream.of(queueSender, topicSender);
    }
}