package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericProcessorsTest {
    @Test
    void testIdProcessor() {
        //Arrange
        var inputData = 1;
        var result = new ArrayList<Integer>();

        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::idProcessor);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, result.size());
        assertEquals(inputData, result.getFirst());
        objectUnderTest.reachDeInit();
    }

    @Test
    void testIncrementer() {
        //Arrange
        var inputData = 1;
        var expectedResult = 2;
        var result = new ArrayList<Integer>();

        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::incrementer);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, result.size());
        assertEquals(expectedResult, result.getFirst());
        objectUnderTest.reachDeInit();
    }

    @Test
    void testConsoleLogger()
    {
        var inputData = "Hello World!";
        var result = new ArrayList<String>();

        Processor<String, String> objectUnderTest = processor(GenericProcessors::consoleLogger);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, result.size());
        assertEquals(inputData, result.getFirst());
        objectUnderTest.reachDeInit();
    }

    @Test
    void testDuplicator() {
        //Arrange
        var inputData = 1;
        var expectedSize = 2;
        var result = new ArrayList<Integer>();

        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::duplicate);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(expectedSize, result.size());
        assertEquals(inputData, result.get(0));
        assertEquals(inputData, result.get(1));
        objectUnderTest.reachDeInit();
    }

    @Test
    void testDevNull() {
        //Arrange
        var inputData = 1;
        var result = new ArrayList<Integer>();

        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::devNull);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertTrue(result.isEmpty());
    }
}
