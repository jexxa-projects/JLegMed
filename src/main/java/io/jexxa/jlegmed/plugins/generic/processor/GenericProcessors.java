package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.common.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.Context;

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

    public static <T> T duplicate(T data, Context context)
    {
        if (!context.isProcessedAgain()) {
            context.processAgain();
        }
        return data;
    }

    private GenericProcessors()
    {
      //Private constructor
    }
}
