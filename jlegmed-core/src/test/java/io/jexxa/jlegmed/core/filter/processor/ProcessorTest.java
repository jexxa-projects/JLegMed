package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static io.jexxa.jlegmed.core.filter.FilterProperties.filterPropertiesOf;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var filterProperties = filterPropertiesOf("someProperties", new Properties());
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor((data, context) -> data + context.propertiesName());

        objectUnderTest.useProperties(filterProperties);
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(1 , receivingPipe.getCollectedData().size());
        assertEquals("Hello World"+filterProperties.name(), receivingPipe.getCollectedData().get(0));
    }

    @Test
    void testProcessAgain()
    {
        //Arrange - test a filter that needs multiple processing steps for single input data
        var receivingPipe = new CollectingInputPipe<String>();
        Processor<String, String> objectUnderTest = processor((data, processorContext) ->
        {
            // Here we tell the processor that we must be called again
            if (!processorContext.processingState().isProcessingAgain()) {
                processorContext.processingState().processAgain();
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

}
