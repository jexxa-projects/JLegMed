package io.jexxa.jlegmed.processor;

import io.jexxa.jlegmed.Message;

import java.util.Properties;

public interface PropertiesProcessor {
    Message process(Message message, Properties properties);
}
