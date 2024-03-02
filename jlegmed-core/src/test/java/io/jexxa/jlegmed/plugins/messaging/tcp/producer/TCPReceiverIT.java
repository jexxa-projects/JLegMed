package io.jexxa.jlegmed.plugins.messaging.tcp.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.messaging.tcp.TCPConnection;
import io.jexxa.jlegmed.plugins.messaging.tcp.TCPConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPReceiverIT {
    private static JLegMed jLegMed;
    @BeforeEach
    void init() {
        jLegMed = new JLegMed(TCPReceiverIT.class)
                .useTechnology(TCPConnectionPool.class)
                .disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void testTCPReceiver()
    {
        //Arrange
        var messageCollector = new Stack<String>();

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from( TCPReceiver::receiveTextMessage).useProperties("test-tcp-sender")

                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();
        sendMessageMultipleTimes("Hello World\n", 3);

        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    @Test
    void testTCPReceiverOneMessagePerConnection()
    {
        var messageCollector = new Stack<String>();

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
