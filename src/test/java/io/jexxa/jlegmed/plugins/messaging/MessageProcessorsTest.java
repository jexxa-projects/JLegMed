package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageProcessors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageProcessorsTest {


    @ParameterizedTest
    @MethodSource("provideMessageConfiguration")
    void testSendAsJSON(MessageConfiguration messageConfiguration)
    {
        //Arrange
        var inputData = "Hello World!";
        var receivingPipe = new CollectingInputPipe<String>();

        Processor<String, String> objectUnderTest = processor(MessageProcessors::sendAsJSON);
        objectUnderTest.useConfig(messageConfiguration);

        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(inputData, receivingPipe.getCollectedData().get(0));
        objectUnderTest.reachDeInit();
    }

    private static Stream<MessageConfiguration> provideMessageConfiguration()
    {
        return Stream.of(queue(("MyQueue")), topic(("MyTopic")));
    }
}