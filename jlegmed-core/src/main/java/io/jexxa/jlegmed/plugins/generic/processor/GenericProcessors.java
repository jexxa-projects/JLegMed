package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterContext;

public class GenericProcessors {

    public static <T> T idProcessor(T data) {
        return data;
    }

    public static Integer incrementer(Integer counter) {
        return ++counter;
    }

    public static <T> T consoleLogger(T data)
    {
        SLF4jLogger.getLogger(GenericProcessors.class).info( "Data : {}", data );
        return data;
    }

    public static <T> T duplicate(T data, FilterContext context)
    {
        var filterState = context.processingState();

        if (!filterState.isProcessingAgain()) {
            filterState.processAgain();
        }
        return data;
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
