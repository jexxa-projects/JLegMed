package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.generic.producer.StreamProducer.streamProducer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class StreamProducerTest {
    @Test
    void testInfiniteStream()
    {
        //Arrange
        var inputData = Stream.iterate(0, i -> i + 1).limit(10);
        var receivingPipe = new CollectingInputPipe<Integer>();

        Producer<Integer> objectUnderTest = streamProducer(inputData);
        objectUnderTest.outputPipe().connectTo(receivingPipe);

        //Act
        objectUnderTest.reachStarted();

        //Assert
        await().atMost(3, SECONDS).until(() -> receivingPipe.getCollectedData().size() == 10);
        objectUnderTest.reachDeInit();
    }
}
