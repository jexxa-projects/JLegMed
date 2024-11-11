package io.jexxa.jlegmed.plugins.generic.muxer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.generic.muxer.Multiplexer.threadedMultiplexer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class ThreadedMultiplexerTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(ThreadedMultiplexerTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }


    @Test
    void testMultiplex() {
        var muxer = threadedMultiplexer(ThreadedMultiplexerTest::multiplexData);
        var messageCollector = new Stack<Integer>();

        // Multiplexing in JLegMed is done by defining multiple flow graphs. This ensures that they can run in parallel.
        // Note: Each flow graph runs in its own transaction. This means that there is no global transaction over the entire
        // flow graph
        // "First flow graph" -Input1"->
        //                               -> "Multiplexer flow graph"
        // "Second flow graph" -Input2"->

        //Arrange the first part of the flow graph
        jlegmed.newFlowGraph("First flow graph")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(muxer::firstInput);

        //Arrange the second part of the flow graph
        jlegmed.newFlowGraph("Second flow graph")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(muxer::secondInput);

        //Arrange the multiplexing part
        jlegmed.newFlowGraph("Multiplexer flow graph ")
                .await(Integer.class).from(muxer)
                .and().consumeWith(messageCollector::push);


        jlegmed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    public static Integer multiplexData(Integer firstData, Integer secondData) {
        return firstData + secondData;
    }


}
