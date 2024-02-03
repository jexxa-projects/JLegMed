package io.jexxa.jlegmed.plugins.messaging.socket.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.socket.processor.TCPConnection;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.jexxa.jlegmed.plugins.messaging.socket.producer.TCPReceiver.tcpReceiver;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPReceiverIT {

    @Test
    void testTCPReceiver() throws IOException
    {
        var messageCollector = new GenericCollector<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from(tcpReceiver( TCPReceiver::receiveLine)).useProperties("test-tcp-sender")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();
        sendMessageMultipleTimes("Hello World\n", 3);

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jLegMed.stop();
    }


    @Test
    void testTCPReceiverOneMessagePerConnection() throws IOException
    {
        var messageCollector = new GenericCollector<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiverOneMessagePerConnection")
                .await(String.class)
                .from(tcpReceiver( TCPReceiver::receiveLine) ).useProperties("test-tcp-sender")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();
        for (int i = 0; i < 3; ++i) {
            sendMessage("Hello World\n");
        }

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jLegMed.stop();
    }



    public static void sendMessageMultipleTimes(String message, int counter) throws IOException
    {
        var tcpConnection = new TCPConnection("localhost", 6665);
        for (int i = 0; i < counter; ++i) {
            tcpConnection.sendMessage(message);
        }
        tcpConnection.close();
    }

    public static void sendMessage(String message) throws IOException
    {
       sendMessageMultipleTimes(message, 1);
    }
}
