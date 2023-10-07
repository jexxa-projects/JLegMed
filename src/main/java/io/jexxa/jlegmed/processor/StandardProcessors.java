package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Message;
import io.jexxa.jlegmed.jexxacp.common.wrapper.logger.SLF4jLogger;

public class StandardProcessors {

    public static Message idProcessor(Message message) {
        return message;
    }
    public static Message consoleLogger(Message message)
    {
        SLF4jLogger.getLogger(StandardProcessors.class).info( "Data : {}", message.getData() );
        return message;
    }

    private StandardProcessors()
    {
      //Private constructor
    }
}
