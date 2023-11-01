package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericProcessorsTest {
    @Test
    void testIdProcessor() {
        //Arrange
        var inputData = 1;
        var receivingPipe = new CollectingInputPipe<Integer>();

        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::idProcessor);
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(inputData, receivingPipe.getCollectedData().get(0));
        objectUnderTest.reachDeInit();
    }

    @Test
    void testIncrementer() {
        //Arrange
        var inputData = 1;
        var expectedResult = 2;
        var receivingPipe = new CollectingInputPipe<Integer>();
        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::incrementer);
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(expectedResult, receivingPipe.getCollectedData().get(0));
        objectUnderTest.reachDeInit();
    }

    @Test
    void testConsoleLogger()
    {
        var inputData = "Hello World!";
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor(GenericProcessors::consoleLogger);
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(inputData, receivingPipe.getCollectedData().get(0));
        objectUnderTest.reachDeInit();
    }

    @Test
    void testDuplicator() {
        //Arrange
        var inputData = 1;
        var expectedSize = 2;
        var receivingPipe = new CollectingInputPipe<Integer>();
        Processor<Integer, Integer> objectUnderTest = processor(GenericProcessors::duplicate);
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.process(inputData);

        //Assert
        assertEquals(expectedSize, receivingPipe.getCollectedData().size());
        assertEquals(inputData, receivingPipe.getCollectedData().get(0));
        assertEquals(inputData, receivingPipe.getCollectedData().get(1));
        objectUnderTest.reachDeInit();
    }

}
