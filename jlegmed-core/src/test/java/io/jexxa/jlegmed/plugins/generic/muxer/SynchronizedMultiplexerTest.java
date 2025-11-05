package io.jexxa.jlegmed.plugins.generic.muxer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static io.jexxa.jlegmed.plugins.generic.muxer.Multiplexer.synchronizedMultiplexer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynchronizedMultiplexerTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(SynchronizedMultiplexerTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void timeoutMultiplexData() {
        var atomicInteger = new AtomicInteger(1);

        var muxer = synchronizedMultiplexer(SynchronizedMultiplexerTest::multiplexData, Duration.of(50, ChronoUnit.MILLIS));
        var errorCollector = new Stack<ProcessingError<Integer>>();
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
                .receive(Integer.class).from(atomicInteger::incrementAndGet).onError(errorCollector::push)
                .and().consumeWith(muxer::firstInput);

        //Arrange the multiplexing part
        jlegmed.newFlowGraph("Multiplexer flow graph ")
                .await(Integer.class).from(muxer)
                .and().consumeWith(messageCollector::push);


        jlegmed.start();

        await().atMost(3, SECONDS).until(() -> errorCollector.size() >= 3);
        assertTrue(messageCollector.empty());
    }


    @Test
    void syncMultiplexData() {
        var atomicInteger = new AtomicInteger(0);

        var muxer = synchronizedMultiplexer(SynchronizedMultiplexerTest::multiplexData, Duration.of(5, ChronoUnit.SECONDS));
        var errorCollector = new Stack<ProcessingError<Integer>>();
        var messageCollector = new Stack<Integer>();

        // Multiplexing in JLegMed is done by defining multiple flow graphs. This ensures that they can run in parallel.
        // Note: Each flow graph runs in its own transaction. This means that there is no global transaction over the entire
        // flow graph
        // "First flow graph" -Input1"->
        //                               -> "Multiplexer flow graph"
        // "Second flow graph" -Input2"->
        //Arrange the multiplexing part
        jlegmed.newFlowGraph("Multiplexer flow graph ")
                .await(Integer.class).from(muxer)
                .and().consumeWith(messageCollector::push);

        //Arrange the first part of the flow graph
        jlegmed.newFlowGraph("First flow graph")
                .every(100, MILLISECONDS)
                .receive(Integer.class).from(atomicInteger::incrementAndGet).onError(errorCollector::push)
                .and().consumeWith(muxer::firstInput);


        //Arrange the first part of the flow graph
        jlegmed.newFlowGraph("Second flow graph")
                .every(100, MILLISECONDS)
                .receive(Integer.class).from(atomicInteger::incrementAndGet).onError(errorCollector::push)
                .and().consumeWith(muxer::secondInput);



        jlegmed.start();

        await().atMost(10, SECONDS).until(() -> messageCollector.size() >= 3);

        if (!errorCollector.empty())
        {
            errorCollector.forEach(System.out::println);
        }

        assertTrue(errorCollector.empty());
    }

    public static Integer multiplexData(Integer firstData, Integer secondData) {
        assertEquals(1, (firstData + secondData) % 2);
        return firstData + secondData;
    }
}
