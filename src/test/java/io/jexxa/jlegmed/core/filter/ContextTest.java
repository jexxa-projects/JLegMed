package io.jexxa.jlegmed.core.filter;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.filter.PropertiesConfig.properties;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextTest {

    @Test
    void getProperties() {
        //Arrange
        var filterConfig = new FilterConfig();
        var jlegmed = new JLegMed(ContextTest.class);
        var objectUnderTest = new Context(jlegmed.getProperties());

        filterConfig.setProperties(properties("test-jms-connection"));
        objectUnderTest.setFilterConfig(filterConfig);


        //Act
        var result = objectUnderTest.getProperties();

        //Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().containsKey("java.naming.factory.initial"));
        assertTrue(result.get().containsKey("java.naming.provider.url"));
        assertTrue(result.get().containsKey("java.naming.user"));
        assertTrue(result.get().containsKey("java.naming.password"));

    }
}