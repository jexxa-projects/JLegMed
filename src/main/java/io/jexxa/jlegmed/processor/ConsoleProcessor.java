package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Message;
import io.jexxa.jlegmed.jexxacp.common.wrapper.logger.SLF4jLogger;

public class  ConsoleProcessor implements Processor {

    public Message process(Message data)
    {
        SLF4jLogger.getLogger(ConsoleProcessor.class).info( "{} : {}", this, data.getData() );
        return data;
    }

}
