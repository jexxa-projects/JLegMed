package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.scheduledProducer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FlowGraphConfigurationTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphConfigurationTest.class).disableBanner();
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
        var messageCollector = new Stack<String>();

        jlegmed.newFlowGraph("UseProperties")

                .await(String.class)

                // The producer appends some properties-information such as name ...
                .from( scheduledProducer(context -> "Hello World" + context.propertiesName()))
                // ... that is injected by method useProperties. The properties are read from resources/jlegmed-application.properties. (@see <a href="https://github.com/jexxa-projects/JLegMed/blob/main/jlegmed-core/src/test/resources/jlegmed-application.properties">here</a>)
                .useProperties(propertiesPrefix)

                .and().processWith( processor(GenericProcessors::idProcessor ))
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        assertEquals("Hello World" + propertiesPrefix, messageCollector.toArray()[0]);
        assertEquals("Hello World" + propertiesPrefix, messageCollector.toArray()[1]);
        assertEquals("Hello World" + propertiesPrefix, messageCollector.toArray()[2]);
    }


    @Test
    void testInvalidProperties() {
        //Arrange
        var propertiesPrefix = "test-invalid-properties"; // This prefix is not defined in the properties file

        var objectUnderTest = jlegmed.newFlowGraph("InvalidProperties")

                .await(String.class)
                .from( scheduledProducer(context -> "Hello World" + context.propertiesName()));

        //Act / Assert - If the propertiesPrefix is not defined in the properties file, an exception is thrown
        assertThrows( IllegalArgumentException.class, () -> objectUnderTest.useProperties(propertiesPrefix));
    }
}
