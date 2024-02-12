package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.generic.producer.BiFunctionMultiplexer.multiplexer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class BiFunctionMultiplexerTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(BiFunctionMultiplexerTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }


    @Test
    void testMultiplex() {
        var muxer = multiplexer(BiFunctionMultiplexerTest::multiplexData);
        var messageCollector = new Stack<Integer>();

        //Arrange first flow graph
        jlegmed.newFlowGraph("First flow graph")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(muxer::firstInput);

        //Arrange second flow graph
        jlegmed.newFlowGraph("Second flow graph")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().consumeWith(muxer::secondInput);

        //Arrange multiplex
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
