package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.core.Message;
import io.jexxa.jlegmed.common.logger.SLF4jLogger;

public class GenericProcessors {

    public static Message idProcessor(Message message) {
        return message;
    }
    public static Message consoleLogger(Message message)
    {
        SLF4jLogger.getLogger(GenericProcessors.class).info( "Data : {}", message.getData() );
        return message;
    }

    private GenericProcessors()
    {
      //Private constructor
    }
}
