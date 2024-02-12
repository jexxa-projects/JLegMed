package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.adapterapi.invocation.DefaultInvocationHandler.GLOBAL_SYNCHRONIZATION_OBJECT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class MuxFlowGraphTest {
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
        jlegmed.newFlowGraph("Send messages")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)
                .and().processWith(multiplexer::receiveFirstCounter)
                .and().consumeWith(messageCollector::push);


        // Receive message via queue again
        jlegmed.newFlowGraph("Receive messages from queue")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().consumeWith(multiplexer::receiveSecondCounter);

        jlegmed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    public static class Multiplexer
    {

        private Integer secondInteger = null;

        int receiveFirstCounter(Integer integer) {
            synchronized (GLOBAL_SYNCHRONIZATION_OBJECT) {
                while (secondInteger == null /*&& !JLegMed.terminateApplication*/) {
                    try {
                        GLOBAL_SYNCHRONIZATION_OBJECT.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread Interrupted");
                    }
                }
                var tempQueueInteger = secondInteger;
                secondInteger = null;
                GLOBAL_SYNCHRONIZATION_OBJECT.notifyAll();
                return integer + tempQueueInteger;
            }
        }


        void receiveSecondCounter(Integer integer) {
            synchronized (GLOBAL_SYNCHRONIZATION_OBJECT) {
                secondInteger = integer;
                GLOBAL_SYNCHRONIZATION_OBJECT.notifyAll();
                while (secondInteger != null /*&& !JLegMed.terminateApplication*/) {
                    try {
                        GLOBAL_SYNCHRONIZATION_OBJECT.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread Interrupted");
                    }
                }
            }
        }

    }


}
