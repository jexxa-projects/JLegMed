package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.jexxacp.common.wrapper.logger.SLF4jLogger;

public class  ConsoleProcessor implements Processor {

    public <T> T process(T data)
    {
        SLF4jLogger.getLogger(ConsoleProcessor.class).info( "{} : {}", this, data );
        return data;
    }

}
