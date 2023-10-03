package io.jexxa.jlegmed.jexxacp.messaging;

import io.jexxa.jlegmed.jexxacp.messaging.logging.MessageLogger;

import java.util.Properties;

public class MessageSenderPool {
    public static MessageSender getMessageSender(Properties properties)
    {
        return new MessageLogger();
    }
}
