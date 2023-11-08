package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualFlowgraphTest {
    
    @Test
    void manualFlowgraphTest() {
        //Arrange
        GenericCollector<String> genericCollector = new GenericCollector<>();

        //Create a source and producers
        FunctionalProducer<String> sourceFilter = producer( () -> "Hello World" );
        Processor<String, String> idProcessor = processor(data -> data );
        Processor<String, String> logProcessor = processor( data -> {SLF4jLogger.getLogger(ManualFlowgraphTest.class).info(data); return data;});
        Processor<String, String> sinkFilter = processor(genericCollector::collect );

        //Connect all filters
        sourceFilter.outputPipe().connectTo(idProcessor.inputPipe());
        idProcessor.outputPipe().connectTo(logProcessor.inputPipe());
        logProcessor.outputPipe().connectTo(sinkFilter.inputPipe());

        //Start filters
        sourceFilter.reachStarted();
        idProcessor.reachStarted();
        logProcessor.reachStarted();
        sinkFilter.reachStarted();

        //Act
        sourceFilter.produceData();

        //Assert
        assertEquals(1, genericCollector.getNumberOfReceivedMessages());
    }
}
