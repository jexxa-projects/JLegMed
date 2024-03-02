package io.jexxa.jlegmed.plugins.messaging.tcp;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.tcp.producer.TCPReceiver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static io.jexxa.jlegmed.plugins.messaging.tcp.TCPMessagingIT.TestMessage.testMessage;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPMessagingIT {
    private static JLegMed jLegMed;
    @BeforeEach
    void init() {
        jLegMed = new JLegMed(TCPMessagingIT.class)
                .useTechnology(TCPConnectionPool.class)
                .disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void testTCPSender() {
        var messageCollector = new Stack<String>();

        // Create a flow graph to listen on incoming text messages via TCP
        jLegMed.newFlowGraph("TCP Listener")
                .await(String.class)
                .from( TCPReceiver::receiveTextMessage).useProperties("test-tcp-receiver")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );


        // Create a flow graph to send text messages via TCP connection
        jLegMed.newFlowGraph("TCP Sender")
                .every(500, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().consumeWith( TCPConnection::sendTextMessage ).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }

    @Test
    void testTCPJSONSender() {
        var counter = new AtomicInteger();
        var messageCollector = new Stack<TestMessage>();

        // Create a flow graph to listen on incoming json messages from TCP port
        jLegMed.newFlowGraph("testTCPReceiver")
                .await(TestMessage.class)
                .from( TCPReceiver::receiveJSON ).useProperties("test-tcp-receiver")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );

        // Create a flow graph to send text messages via TCP connection
        jLegMed.newFlowGraph("testTCPSender")
                .every(500, MILLISECONDS)
                .receive(TestMessage.class).from(() -> testMessage(counter.getAndIncrement(),"Hello World"))

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( TCPConnection::sendJSONMessage ).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }

    record TestMessage(int counter, String message) {
        public static TestMessage testMessage(int counter, String message) { return new TestMessage(counter, message); }
    }

}
