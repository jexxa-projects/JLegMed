package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.generic.producer.StreamProducer.streamProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class StreamProducerTest {
    @Test
    void testInfiniteStream()
    {
        //Arrange
        Stream<Integer> infiniteStream1 = Stream.iterate(0, i -> i + 2);
        Stream<Integer> infiniteStream2 = Stream.iterate(0, i -> i + 2);

        var messageCollector1 = new MessageCollector<Integer>();
        var messageCollector2 = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(StreamProducerTest.class);

        jlegmed.newFlowGraph("FlowGraphTest1")

                .await(Integer.class)
                .from(streamProducer(infiniteStream1))

                .and().processWith( messageCollector1::collect );


        jlegmed.newFlowGraph("FlowGraphTest2")

                .await(Integer.class)
                .from(streamProducer(infiniteStream2))
                .and().processWith( messageCollector2::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS)
                .pollInterval(50, MILLISECONDS)
                .until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3
                        && messageCollector2.getNumberOfReceivedMessages() >=3
                );
        jlegmed.stop();
    }

}
