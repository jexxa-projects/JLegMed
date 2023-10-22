package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.common.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.flowgraph.Context;

public class GenericProcessors {

    public static Object idProcessor(Object data) {
        return data;
    }

    public static Integer incrementer(Integer counter) {
        return ++counter;
    }

    public static Object consoleLogger(Object data)
    {
        SLF4jLogger.getLogger(GenericProcessors.class).info( "Data : {}", data );
        return data;
    }

    public static Object duplicate(Object data, Context context)
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
