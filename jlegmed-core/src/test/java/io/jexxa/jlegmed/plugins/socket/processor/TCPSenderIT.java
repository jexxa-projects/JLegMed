package io.jexxa.jlegmed.plugins.socket.processor;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.socket.processor.TCPSender.tcpSender;
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
                .from(tcpReceiver(context -> context.bufferedReader().readLine())).useProperties("test-tcp-receiver")
                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect );


        jLegMed.newFlowGraph("testTCPSender")
                .each(500, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World\n")

                .and().consumeWith(tcpSender((data, context) -> context.bufferedWriter().write(data) )).useProperties("test-tcp-sender");

        //Act
        jLegMed.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);

        jLegMed.stop();
    }

}
