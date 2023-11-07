package io.jexxa.jlegmed.plugins.socket.processor;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.socket.processor.TCPSender.createTCPSender;
import static io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver.createTCPReceiver;
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
                .from(createTCPReceiver( 6665, context -> context.bufferedReader().readLine()))
                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::collect);


        jLegMed.newFlowGraph("testTCPSender")
                .each(500, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World\n")
                .and().processWith( createTCPSender(6665, "localhost",
                        (data, context) -> { context.bufferedWriter().write(data); context.bufferedWriter().flush(); return data;  }));
        //Act
        jLegMed.start();

        System.out.println("await ...");
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        System.out.println("Stops ...");

        jLegMed.stop();
    }

}
