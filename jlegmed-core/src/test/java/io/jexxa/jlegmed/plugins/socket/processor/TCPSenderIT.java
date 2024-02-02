package io.jexxa.jlegmed.plugins.socket.processor;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.jexxa.jlegmed.plugins.socket.processor.TCPSender.tcpSender;
import static io.jexxa.jlegmed.plugins.socket.processor.TCPSenderIT.TestMessage.testMessage;
import static io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver.tcpReceiver;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPSenderIT {

    @Test
    void testTCPSender() {
        var messageCollector = new GenericCollector<String>();
        JLegMed jLegMed = new JLegMed(TCPSenderIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from(tcpReceiver( TCPReceiver::receiveLine)).useProperties("test-tcp-receiver")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect );


        jLegMed.newFlowGraph("testTCPSender")
                .every(500, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().consumeWith(tcpSender( TCPSender::sendLine)).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        jLegMed.stop();
    }

    @Test
    void testTCPJSONSender() {
        var counter = new AtomicInteger();
        var messageCollector = new GenericCollector<TestMessage>();
        JLegMed jLegMed = new JLegMed(TCPSenderIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(TestMessage.class)
                .from(tcpReceiver( TCPReceiver::receiveAsJSON )).useProperties("test-tcp-receiver")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect );


        jLegMed.newFlowGraph("testTCPSender")
                .every(500, MILLISECONDS)
                .receive(TestMessage.class).from(() -> testMessage(counter.getAndIncrement(),"Hello World"))

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith(tcpSender( TCPSender::sendAsJSON )).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        jLegMed.stop();
    }

    record TestMessage(int counter, String message) {
        public static TestMessage testMessage(int counter, String message) { return new TestMessage(counter, message); }
    }
}
