package io.jexxa.jlegmed.plugins.messaging.tcp;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.tcp.producer.TCPReceiver;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static io.jexxa.jlegmed.plugins.messaging.tcp.TCPMessagingIT.TestMessage.testMessage;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPMessagingIT {

    @Test
    void testTCPSender() {
        TCPConnectionPool.init();
        var messageCollector = new Stack<String>();
        JLegMed jLegMed = new JLegMed(TCPMessagingIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from( TCPReceiver::receiveMessage ).useProperties("test-tcp-receiver")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );


        jLegMed.newFlowGraph("testTCPSender")
                .every(500, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().consumeWith( TCPConnection::sendTextMessage ).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);

        jLegMed.stop();
    }

    @Test
    void testTCPJSONSender() {
        TCPConnectionPool.init();

        var counter = new AtomicInteger();
        var messageCollector = new Stack<TestMessage>();
        JLegMed jLegMed = new JLegMed(TCPMessagingIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(TestMessage.class)
                .from( TCPReceiver::receiveJSON ).useProperties("test-tcp-receiver")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );


        jLegMed.newFlowGraph("testTCPSender")
                .every(500, MILLISECONDS)
                .receive(TestMessage.class).from(() -> testMessage(counter.getAndIncrement(),"Hello World"))

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( TCPConnection::sendJSONMessage ).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);

        jLegMed.stop();
    }

    record TestMessage(int counter, String message) {
        public static TestMessage testMessage(int counter, String message) { return new TestMessage(counter, message); }
    }

}
