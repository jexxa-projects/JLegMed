package io.jexxa.jlegmed.core.flowgraph;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.dto.NewContract;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.ProducerMode.ONLY_ONCE;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.ProducerMode.UNTIL_STOPPED;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.inputStream;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.activeProducer;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.schedule;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowGraphConfigurationTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphBuilderTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testConfigureProducer() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("testConfigureProducer")

                .await(Integer.class)
                // Here we configure a producer that produces a counter in a specific interval
                .from(activeProducer(GenericProducer::counter)).filterConfig(schedule(50, MILLISECONDS)).and()

                .processWith( processor(GenericProcessors::idProcessor )).and()
                .processWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }
    @Test
    void testFilterConfigUntilStopped() {
        //Arrange
        var messageCollector = new GenericCollector<NewContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        jlegmed.newFlowGraph("ProducerURL")

                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStream(inputStream)).filterConfig(UNTIL_STOPPED)

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We must receive > 1 messages
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() > 1);
    }


    @Test
    void testFilterConfigOnlyOnce() {
        //Arrange
        var messageCollector = new GenericCollector<>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(new NewContract(1)).getBytes());

        jlegmed.newFlowGraph("ProducerURLOnlyOnce")

                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(inputStream(inputStream)).filterConfig(ONLY_ONCE)

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We receive only a single message
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() == 1);
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }
}
