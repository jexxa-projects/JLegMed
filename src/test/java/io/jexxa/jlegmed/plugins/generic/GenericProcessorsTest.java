package io.jexxa.jlegmed.plugins.generic;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class GenericProcessorsTest {
    @Test
    void testFlowGraphIncrementer() {
        //Arrange
        var messageCollector = new MessageCollector<Integer>();
        var jlegmed = new JLegMed(GenericProcessorsTest.class).disableBanner();

        jlegmed.newFlowGraph("Incrementer")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from( () -> 1)

                .and().processWith( GenericProcessors::incrementer )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

}
