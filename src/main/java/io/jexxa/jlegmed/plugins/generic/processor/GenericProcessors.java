package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.common.logger.SLF4jLogger;

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

    private GenericProcessors()
    {
      //Private constructor
    }
}
