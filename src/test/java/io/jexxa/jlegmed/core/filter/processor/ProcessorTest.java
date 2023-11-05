package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Properties;

import static io.jexxa.jlegmed.core.filter.FilterProperties.filterPropertiesOf;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessorTest {
    @Test
    void testFunctionProcessor()
    {
        //Arrange
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor(data -> data);

        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(1 , receivingPipe.getCollectedData().size());
        assertEquals("Hello World", receivingPipe.getCollectedData().get(0));
    }

    @Test
    void testBiFunctionProcessor()
    {
        //Arrange
        var propertiesName = "someProperties";
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor((data, filterContext) -> data + filterContext.filterProperties().orElseThrow().propertiesName());

        objectUnderTest.useProperties(filterPropertiesOf(propertiesName, new Properties()));
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(1 , receivingPipe.getCollectedData().size());
        assertEquals("Hello World"+propertiesName, receivingPipe.getCollectedData().get(0));
    }

    @Test
    void testProcessAgain()
    {
        //Arrange - test a filter that needs multiple processing steps for single input data
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor((data, filterContext) ->
        {
            // Here we tell the processor that we must be called again
            if (!filterContext.processingState().isProcessingAgain()) {
                filterContext.processingState().processAgain();
            }
            return data;
        });

        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(2 , receivingPipe.getCollectedData().size());
        assertEquals("Hello World", receivingPipe.getCollectedData().get(0));
    }
    @Test
    void testBiFunctionProcessorThrowing()
    {
        //Arrange - We do not configure properties here to test that filter throws an exception
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor((data, filterContext) -> data + filterContext.filterProperties().orElseThrow().propertiesName());

        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act / Assert
        var inputPipe = objectUnderTest.inputPipe();
        assertThrows( NoSuchElementException.class, () -> inputPipe.receive("Hello World"));
    }
}
