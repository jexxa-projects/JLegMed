package io.jexxa.jlegmed.core.filter.producer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Properties;

import static io.jexxa.jlegmed.core.filter.FilterProperties.filterPropertiesOf;
import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalProducerTest {

    @Test
    void testSupplyingProducer()
    {
        //Arrange
        var result = new ArrayList<String>();
        var objectUnderTest = producer(() -> "Hello World");
        objectUnderTest.outputPipe().connectTo(result::add);

        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, result.size());
        assertEquals("Hello World", result.get(0));
    }

    @Test
    void testFunctionProducer()
    {
        //Arrange
        var filterProperties = filterPropertiesOf("someProperties", new Properties());
        var result = new ArrayList<String>();
        var objectUnderTest = producer(context -> "Hello World" + context.propertiesName() );

        objectUnderTest.useProperties(filterProperties);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, result.size());
        assertEquals("Hello World"+ filterProperties.name() , result.get(0));
    }

    @Test
    void testBiFunctionProducer()
    {
        //Arrange
        var filterProperties = filterPropertiesOf("someProperties", new Properties());
        var result = new ArrayList<String>();
        FunctionalProducer<String> objectUnderTest = producer( (filterContext, dataType) ->
                "Hello World" + filterContext.propertiesName() + dataType.getSimpleName());

        objectUnderTest.producingType(String.class);
        objectUnderTest.useProperties(filterProperties);
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, result.size());
        assertEquals("Hello World" + filterProperties.name() + String.class.getSimpleName(),
                result.get(0));
    }

    @Test
    void testProduceAgain()
    {
        //Arrange
        var result = new ArrayList<String>();
        FunctionalProducer<String> objectUnderTest = producer(filterContext -> {
            // Here we tell the producer that we must be called again
            if (!filterContext.processingState().isProcessingAgain()) {
                filterContext.processingState().processAgain();
            }
            return "Hello World";
        });

        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(2, result.size());
        assertEquals("Hello World", result.get(0));
        assertEquals("Hello World", result.get(1));
    }

}
