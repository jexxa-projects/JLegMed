package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.jexxacp.common.wrapper.logger.SLF4jLogger;

public class IDProcessor implements Processor {

    @Override
    public <T> T process(T data) {
        SLF4jLogger.getLogger(IDProcessor.class).info( "{} : {}", this, data );
        return data;
    }
}
