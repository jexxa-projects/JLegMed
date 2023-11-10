package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.jexxa.jlegmed.core.filter.processor.Processor.consumer;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualFlowgraphTest {
    
    @Test
    void manualFlowgraphTest() {
        //Arrange
        var result = new ArrayList<String>();

        //Create a source and producers
        FunctionalProducer<String> sourceFilter = producer( () -> "Hello " );
        Processor<String, String> processorFilter = processor(data -> data + "World" );
        Processor<String, String> sinkFilter = consumer( data -> result.add(data) );

        //Connect all filters
        sourceFilter.outputPipe().connectTo(processorFilter.inputPipe());
        processorFilter.outputPipe().connectTo(sinkFilter.inputPipe());

        //Start filters
        sourceFilter.reachStarted();
        processorFilter.reachStarted();
        sinkFilter.reachStarted();

        //Act
        sourceFilter.produceData();

        //Assert
        assertEquals(1, result.size());
        assertEquals("Hello World", result.get(0));
    }
}
