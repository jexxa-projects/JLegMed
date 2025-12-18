package io.jexxa.jlegmed.plugins.persistence.timer;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilsTest {

    @Test
    void testWithLookBack() {
        //Arrange
        var begin = Instant.now().minus(1, ChronoUnit.MINUTES);
        var end = Instant.now();
        var objectUnderTest = new TimeInterval(begin, end);

        //Act
        var result = TimeUtils.startWithLookBack(objectUnderTest, Duration.ofMinutes(10));

        //Assert
        assertEquals(begin.minus(Duration.ofMinutes(10)), result.begin());
        assertEquals(end, result.end());
    }


    @Test
    void testWithLookAhead() {
        //Arrange
        var begin = Instant.now().minus(1, ChronoUnit.MINUTES);
        var end = Instant.now();
        var objectUnderTest = new TimeInterval(begin, end);

        //Act
        var result = TimeUtils.endWithLookAhead(objectUnderTest, Duration.ofMinutes(10));

        //Assert
        assertEquals(begin, result.begin());
        assertEquals(end.plus(Duration.ofMinutes(10)), result.end());
    }

}