package io.jexxa.jlegmed.common.wrapper.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SLF4jLogger
{
    public static Logger getLogger(Class<?> clazz)
    {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(String prefix)
    {
        return LoggerFactory.getLogger(prefix);
    }

    private SLF4jLogger()
    {
        //Private constructor
    }
}