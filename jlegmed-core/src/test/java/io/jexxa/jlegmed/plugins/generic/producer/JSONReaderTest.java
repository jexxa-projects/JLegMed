package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.flowgraph.TestFilter;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static io.jexxa.jlegmed.core.flowgraph.TestFilter.NewContract.newContract;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.jsonStreamOnlyOnce;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.jsonStreamUntilStopped;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONReaderTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(JSONReaderTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testFactoryMethodUntilStopped() {
        //Arrange
        var messageCollector = new GenericCollector<TestFilter.NewContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(newContract(1)).getBytes());

        jlegmed.newFlowGraph("testFactoryMethodUntilStopped")

                .every(10, MILLISECONDS)

                // Here we configure a producer using a factory method telling us the configuration mode
                .receive(TestFilter.NewContract.class).from(jsonStreamUntilStopped(inputStream))

                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert - We must receive > 1 messages
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() > 1);
    }


    @Test
    void testFactoryMethodOnlyOnce() {
        //Arrange
        var messageCollector = new GenericCollector<>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(newContract(1)).getBytes());

        jlegmed.newFlowGraph("FilterConfigOnlyOnce")

                .every(10, MILLISECONDS)
                // Here we configure a producer using a factory method telling us the configuration mode
                .receive(TestFilter.NewContract.class).from(jsonStreamOnlyOnce(inputStream))

                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert - We receive only a single message
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() == 1);
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }


}
