package io.jexxa.jlegmed.plugins.socket.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver.tcpReceiver;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPReceiverIT {

    @Test
    @Disabled
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
    @Disabled
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
        var clientSocket = new Socket("localhost", 6665);
        var bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        for (int i = 0; i < counter; ++i) {
            bufferedWriter.write(message);
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        clientSocket.close();
    }

    public static void sendMessage(String message) throws IOException
    {
       sendMessageMultipleTimes(message, 1);
    }
}
