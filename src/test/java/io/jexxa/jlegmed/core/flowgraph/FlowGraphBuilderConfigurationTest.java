package io.jexxa.jlegmed.core.flowgraph;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.core.flowgraph.TestFilter.NewContract.newContract;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.jsonStreamOnlyOnce;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.jsonStreamUntilStopped;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.activeProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FlowGraphBuilderConfigurationTest {
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
    void testUseProperties() {
        //Arrange
        var propertiesPrefix = "test-jms-connection"; // This is the prefix used in properties file
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("UseProperties")

                .await(String.class)
                // Here we configure a producer that produces a counter in a specific interval
                .from(activeProducer(
                        context -> "Hello World" + context.filterProperties().orElseThrow().propertiesName())
                ).useProperties(propertiesPrefix).and()

                .processWith( processor(GenericProcessors::idProcessor )).and()
                .processWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World" + propertiesPrefix, messageCollector.getMessages().get(0));
        assertEquals("Hello World" + propertiesPrefix, messageCollector.getMessages().get(1));
        assertEquals("Hello World" + propertiesPrefix, messageCollector.getMessages().get(2));
    }

    @Test
    void testFactoryMethodUntilStopped() {
        //Arrange
        var messageCollector = new GenericCollector<TestFilter.NewContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(newContract(1)).getBytes());

        jlegmed.newFlowGraph("testFactoryMethodUntilStopped")

                .each(10, MILLISECONDS)
                // Here we configure a producer using a factory method telling us the configuration mode
                .receive(TestFilter.NewContract.class).from(jsonStreamUntilStopped(inputStream))

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
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

                .each(10, MILLISECONDS)
                // Here we configure a producer using a factory method telling us the configuration mode
                .receive(TestFilter.NewContract.class).from(jsonStreamOnlyOnce(inputStream))

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert - We receive only a single message
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() == 1);
        assertEquals(1, messageCollector.getNumberOfReceivedMessages());
    }


    @Test
    void testInvalidProperties() {
        //Arrange
        var propertiesPrefix = "test-invalid-properties"; // This prefix is not defined in the properties file

        var objectUnderTest = jlegmed.newFlowGraph("UseConfig")

                .await(String.class)
                // Here we configure a producer that produces a counter in a specific interval
                .from(activeProducer(
                        filterContext -> "Hello World" + filterContext.filterProperties().orElseThrow().propertiesName())
                );

        //Act / Assert
        assertThrows( IllegalArgumentException.class, () -> objectUnderTest.useProperties(propertiesPrefix));
    }
}
