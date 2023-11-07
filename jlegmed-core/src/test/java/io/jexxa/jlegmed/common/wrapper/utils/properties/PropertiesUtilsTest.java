package io.jexxa.jlegmed.common.wrapper.utils.properties;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.common.wrapper.jms.JMSProperties.JNDI_FACTORY_KEY;
import static io.jexxa.jlegmed.common.wrapper.jms.JMSProperties.JNDI_PASSWORD_KEY;
import static io.jexxa.jlegmed.common.wrapper.jms.JMSProperties.JNDI_PROVIDER_URL_KEY;
import static io.jexxa.jlegmed.common.wrapper.jms.JMSProperties.JNDI_USER_KEY;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertiesUtilsTest {

    @Test
    void getProperties() {
        //Arrange
        var jlegmed = new JLegMed(PropertiesUtilsTest.class);
        var objectUnderTest = jlegmed.getProperties();

        //Act
        var result = PropertiesUtils.getSubset(objectUnderTest, "test-jms-connection");

        //Assert
        assertTrue(result.containsKey(JNDI_FACTORY_KEY));
        assertTrue(result.containsKey(JNDI_PROVIDER_URL_KEY));
        assertTrue(result.containsKey(JNDI_USER_KEY));
        assertTrue(result.containsKey(JNDI_PASSWORD_KEY));
    }
}