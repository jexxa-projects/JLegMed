package io.jexxa.jlegmed.plugins.generic.processor;

import io.jexxa.jlegmed.core.Content;
import io.jexxa.jlegmed.common.logger.SLF4jLogger;

public class GenericProcessors {

    public static Content idProcessor(Content content) {
        return content;
    }
    public static Content consoleLogger(Content content)
    {
        SLF4jLogger.getLogger(GenericProcessors.class).info( "Data : {}", content.data() );
        return content;
    }

    private GenericProcessors()
    {
      //Private constructor
    }
}
