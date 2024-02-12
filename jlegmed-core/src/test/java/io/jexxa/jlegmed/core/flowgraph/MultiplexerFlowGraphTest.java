package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.producer.BiMultiplexer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MultiplexerFlowGraphTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(AwaitFlowGraphTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }


    @Test
    void testMultiplex() {
        var multiplexer = new Multiplexer();
        var messageCollector = new Stack<Integer>();

        //Arrange
        jlegmed.newFlowGraph("First flow graph")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(multiplexer::receiveFirstCounter);

        // Receive message via queue again
        jlegmed.newFlowGraph("Second flow graph")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().consumeWith(multiplexer::receiveSecondCounter);

        jlegmed.newFlowGraph("Multiplexer flow graph ")
                .await(Integer.class).from(() -> multiplexer)
                .and().consumeWith(messageCollector::push);


        jlegmed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    public static class Multiplexer extends BiMultiplexer<Integer, Integer, Integer>
    {

        void receiveFirstCounter(Integer integer) {
            notifyFirstData(integer);
        }


        void receiveSecondCounter(Integer integer) {
            notifySecondData(integer);
        }

        @Override
        public Integer multiplexData(Integer firstData, Integer secondData) {
            return firstData + secondData;
        }
    }


}
