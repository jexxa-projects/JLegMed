package io.jexxa.jlegmed.core.filter.processor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Properties;

import static io.jexxa.jlegmed.core.filter.FilterProperties.filterPropertiesOf;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorTest {

    @Test
    void testFunctionProcessor2()
    {
        //Arrange
        var result = new ArrayList<String>();
        Processor<String, String> objectUnderTest = processor(data -> data);

        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(1 , result.size());
        assertEquals("Hello World", result.get(0));
    }


    @Test
    void testBiFunctionProcessor()
    {
        //Arrange
        var filterProperties = filterPropertiesOf("someProperties", new Properties());
        var result = new ArrayList<String>();
        Processor<String, String> objectUnderTest = processor((data, context) -> data + context.propertiesName());

        objectUnderTest.useProperties(filterProperties);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(1 , result.size());
        assertEquals("Hello World"+filterProperties.name(), result.get(0));
    }

    @Test
    void testProcessAgain()
    {
        //Arrange - test a filter that needs multiple processing steps for single input data
        var result = new ArrayList<String>();
        Processor<String, String> objectUnderTest = processor(
                (data, processorContext) ->
                {
                    // Here we tell the processor that we must be called again
                    if (!processorContext.processingState().isProcessingAgain()) {
                        processorContext.processingState().processAgain();
                    }
                    return data;
                }
                );

        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.inputPipe().receive("Hello World");

        //Assert
        assertEquals(2 , result.size());
        assertEquals("Hello World", result.get(0));
    }

}
