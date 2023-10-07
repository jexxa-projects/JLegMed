package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Message;
import io.jexxa.jlegmed.jexxacp.common.wrapper.logger.SLF4jLogger;

public class IDProcessor implements Processor {

    @Override
    public Message process(Message data) {
        SLF4jLogger.getLogger(IDProcessor.class).info( "{} : {}", this, data.getData() );
        return data;
    }
}
