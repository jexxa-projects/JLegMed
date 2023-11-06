package io.jexxa.jlegmed.plugins.socket;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver.createTCPReceiver;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class TCPReceiverIT {

    @Test
    void testTCPReceiver() {
        var messageCollector = new GenericCollector<String>();
        JLegMed jLegMed = new JLegMed(TCPReceiverIT.class);

        jLegMed.newFlowGraph("testTCPReceiver")
                .await(String.class)
                .from(createTCPReceiver( 6666, (bufferedReader, bufferedWriter) -> {
                    try {
                        return bufferedReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .and().processWith( GenericProcessors::consoleLogger )
                .and().processWith( messageCollector::collect);
        //Act
        jLegMed.start();

        var tcpClient = new TCPClient();
        tcpClient.start();

        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jLegMed.stop();
    }

    private static class TCPClient extends Thread
    {
        @Override
        public void run()
        {
            try {
                var clientSocket = new Socket("localhost", 6666);
                var bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                bufferedWriter.write("Hello World1\n");
                bufferedWriter.write("Hello World2\n");
                bufferedWriter.write("Hello World3\n");
                bufferedWriter.flush();
                bufferedWriter.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
