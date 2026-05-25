package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.processor.StreamProcessor;
import io.jexxa.jlegmed.core.flowgraph.steps.ProcessorStep;

import static io.jexxa.jlegmed.core.filter.processor.Processor.streamProcessor;
import static io.jexxa.jlegmed.core.flowgraph.steps.ProcessorStep.processorStep;

public class GenericProcessors {

    public static <T> ProcessorStep<T,T> createPassThroughProcessor() {
        return processorStep(data -> data);
    }

    public static Integer incrementer(Integer counter) {
        return ++counter;
    }

    public static <T> T consoleLogger(T data)
    {
        SLF4jLogger.getLogger(GenericProcessors.class).info( "Data : {}", data );
        return data;
    }

    public static <T> StreamProcessor<T,T> createDuplicator()
    {
        return streamProcessor(
                (data, processContext) ->
        {
            var filterState = processContext.processingState();

            if (!filterState.isProcessingAgain()) {
                filterState.processAgain();
            }
            return data;
        });
    }

    @SuppressWarnings("java:S1172")
    public static <T> T devNull(T ignoredData) {
        return null;
    }

    private GenericProcessors()
    {
      //Private constructor
    }
}
