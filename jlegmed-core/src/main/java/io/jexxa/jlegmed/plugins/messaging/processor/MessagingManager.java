package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.drivenadapter.messaging.MessageSenderManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("java:S6548")
public class MessagingManager {
    private static final MessagingManager messageSenderManager = new MessagingManager();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();


    public static MessageSender getMessageSender(String connectionName, Properties properties)
    {
        return getInstance().getInternalMessageSender(connectionName, properties);
    }

    MessageSender getInternalMessageSender(String connectionName, Properties properties)
    {
        messageSenderMap.computeIfAbsent(connectionName,
                key -> MessageSenderManager.getMessageSender(MessagingManager.class, properties)
        );
        return messageSenderMap.get(connectionName);
    }

    static MessagingManager getInstance() {
        return messageSenderManager;
    }

    private MessagingManager()
    {
        // Private constructor
    }
}
