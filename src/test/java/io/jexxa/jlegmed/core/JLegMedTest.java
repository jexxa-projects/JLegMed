package io.jexxa.jlegmed.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class JLegMedTest {
    @Test
    void testVersionInfo()
    {
        //Arrange
        var objectUnderTest= new JLegMed(JLegMedTest.class);

        //Act
        var result = objectUnderTest.getVersion();

        //Assert
        assertFalse(result.buildTimestamp().isEmpty());
        assertFalse(result.version().isEmpty());
        assertFalse(result.projectName().isEmpty());
        assertFalse(result.repository().isEmpty());
    }
}
