package io.jexxa.jlegmed.plugins.generic.producer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        var result = new ArrayList<Integer>();

        var objectUnderTest = streamProducer(inputData);
        objectUnderTest.outputPipe().connectTo(result::add);

        //Act
        objectUnderTest.reachStarted();

        //Assert
        await().atMost(3, SECONDS).until(() -> result.size() == 10);
        objectUnderTest.reachDeInit();
    }
}
