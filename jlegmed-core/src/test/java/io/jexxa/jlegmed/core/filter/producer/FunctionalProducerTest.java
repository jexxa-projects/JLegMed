package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Properties;

import static io.jexxa.jlegmed.core.filter.FilterProperties.filterPropertiesOf;
import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FunctionalProducerTest {

    @Test
    void testSupplyingProducer()
    {
        //Arrange
        CollectingInputPipe<String> inputPipe = new CollectingInputPipe<>();
        var objectUnderTest = producer(() -> "Hello World");
        objectUnderTest.outputPipe().connectTo(inputPipe);

        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, inputPipe.getCollectedData().size());
        assertEquals("Hello World", inputPipe.getCollectedData().get(0));
    }

    @Test
    void testFunctionProducer()
    {
        //Arrange
        var propertiesName = "someProperties";
        CollectingInputPipe<String> inputPipe = new CollectingInputPipe<>();
        var objectUnderTest = producer(filterContext ->
                "Hello World" + filterContext.filterProperties().orElseThrow().propertiesName()
        );

        objectUnderTest.useProperties(filterPropertiesOf(propertiesName, new Properties()));
        objectUnderTest.outputPipe().connectTo(inputPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, inputPipe.getCollectedData().size());
        assertEquals("Hello World"+ propertiesName , inputPipe.getCollectedData().get(0));
    }

    @Test
    void testBiFunctionProducer()
    {
        //Arrange
        var propertiesName = "someProperties";
        CollectingInputPipe<String> inputPipe = new CollectingInputPipe<>();
        FunctionalProducer<String> objectUnderTest = producer( (filterContext, dataType) ->
                "Hello World" + filterContext.filterProperties().orElseThrow().propertiesName() + dataType.getSimpleName()
        );

        objectUnderTest.producingType(String.class);
        objectUnderTest.useProperties(filterPropertiesOf(propertiesName, new Properties()));
        objectUnderTest.outputPipe().connectTo(inputPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, inputPipe.getCollectedData().size());
        assertEquals("Hello World" + propertiesName + String.class.getSimpleName(), inputPipe.getCollectedData().get(0));
    }

    @Test
    void testProduceAgain()
    {
        //Arrange
        CollectingInputPipe<String> inputPipe = new CollectingInputPipe<>();
        FunctionalProducer<String> objectUnderTest = producer(filterContext -> {
            // Here we tell the producer that we must be called again
            if (!filterContext.processingState().isProcessingAgain()) {
                filterContext.processingState().processAgain();
            }
            return "Hello World";
        });

        objectUnderTest.outputPipe().connectTo(inputPipe);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(2, inputPipe.getCollectedData().size());
        assertEquals("Hello World", inputPipe.getCollectedData().get(0));
        assertEquals("Hello World", inputPipe.getCollectedData().get(1));
    }

    @Test
    void testFunctionProducerThrowing()
    {
        //Arrange - Without properties so that our producer throws an exception
        CollectingInputPipe<String> inputPipe = new CollectingInputPipe<>();
        FunctionalProducer<String> objectUnderTest = producer(filterContext ->
                "Hello World" + filterContext.filterProperties().orElseThrow().propertiesName()
        );
        objectUnderTest.outputPipe().connectTo(inputPipe);
        objectUnderTest.reachStarted();

        //Act / Assert
        assertThrows(NoSuchElementException.class, objectUnderTest::produceData);
    }
}
