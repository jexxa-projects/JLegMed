package io.jexxa.jlegmed.plugins.messaging.tcp.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.tcp.TCPConnection;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPReceiverIT {

    @Test
    void testTCPReceiver()
    {
        var messageCollector = new Stack<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from( TCPReceiver::receiveTextMessage).useProperties("test-tcp-sender")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();
        sendMessageMultipleTimes("Hello World\n", 3);

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        jLegMed.stop();
    }


    @Test
    void testTCPReceiverOneMessagePerConnection()
    {
        var messageCollector = new Stack<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiverOneMessagePerConnection")
                .await(String.class)
                .from( TCPReceiver::receiveTextMessage).useProperties("test-tcp-sender")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();
        for (int i = 0; i < 3; ++i) {
            sendMessage("Hello World\n");
        }

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        jLegMed.stop();
    }



    public static void sendMessageMultipleTimes(String message, int counter)
    {
        var tcpConnection = new TCPConnection("localhost", 6665);
        for (int i = 0; i < counter; ++i) {
            tcpConnection.sendMessage(message);
        }
        tcpConnection.close();
    }

    public static void sendMessage(String message)
    {
       sendMessageMultipleTimes(message, 1);
    }
}
