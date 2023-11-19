package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.activeProducer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testUseProperties() {
        //Arrange
        var propertiesPrefix = "test-jms-connection"; // This is the prefix used in properties file
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("UseProperties")

                .await(String.class)

                // The producer appends properties-information...
                .from(activeProducer( context -> "Hello World" + context.propertiesName()))
                // ... that are handed in by useProperties. The properties are read from jlegmed-application.properties
                .useProperties(propertiesPrefix)

                .and().processWith( processor(GenericProcessors::idProcessor ))
                .and().consumeWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals("Hello World" + propertiesPrefix, messageCollector.getMessages().get(0));
        assertEquals("Hello World" + propertiesPrefix, messageCollector.getMessages().get(1));
        assertEquals("Hello World" + propertiesPrefix, messageCollector.getMessages().get(2));
    }


    @Test
    void testInvalidProperties() {
        //Arrange
        var propertiesPrefix = "test-invalid-properties"; // This prefix is not defined in the properties file

        var objectUnderTest = jlegmed.newFlowGraph("UseConfig")

                .await(String.class)
                .from(activeProducer(context -> "Hello World" + context.propertiesName()));

        //Act / Assert - If the propertiesPrefix is not defined in the properties file, an exception is thrown
        assertThrows( IllegalArgumentException.class, () -> objectUnderTest.useProperties(propertiesPrefix));
    }
}
