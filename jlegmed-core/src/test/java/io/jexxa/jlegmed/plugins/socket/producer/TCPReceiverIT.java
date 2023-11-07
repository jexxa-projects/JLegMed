package io.jexxa.jlegmed.plugins.socket.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver.tcpReceiver;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPReceiverIT {

    @Test
    void testTCPReceiver() {
        var messageCollector = new GenericCollector<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from(tcpReceiver( 6666, context -> context.bufferedReader().readLine()))
                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();
        sendMessageMultipleTimes("Hello World\n", 3);

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jLegMed.stop();
    }


    @Test
    void testTCPReceiverOneMessagePerConnection() {
        var messageCollector = new GenericCollector<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiverOneMessagePerConnection")
                .await(String.class)
                .from(tcpReceiver( 6666, context -> context.bufferedReader().readLine()))
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



    public static void sendMessageMultipleTimes(String message, int counter)
    {
        try {
            var clientSocket = new Socket("localhost", 6666);
            var bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            for (int i = 0; i < counter; ++i) {
                bufferedWriter.write(message);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendMessage(String message)
    {
       sendMessageMultipleTimes(message, 1);
    }
}