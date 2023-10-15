package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextTest {

    @Test
    void getProperties() {
        //Arrange
        var jlegmed = new JLegMed(ContextTest.class);
        var objectUnderTest = new Context(jlegmed.getProperties());

        //Act
        var result = objectUnderTest.getProperties("test-jms-connection");

        //Assert
        assertTrue(result.containsKey("java.naming.factory.initial"));
        assertTrue(result.containsKey("java.naming.provider.url"));
        assertTrue(result.containsKey("java.naming.user"));
        assertTrue(result.containsKey("java.naming.password"));

    }
}