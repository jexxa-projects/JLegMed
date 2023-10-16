package io.jexxa.jlegmed.plugins.messaging.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MessagingManager {
    private static final MessagingManager messageSenderManager = new MessagingManager();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();


    public static MessageSender getMessageSender(MessageSender.Configuration configuration, Properties properties)
    {
        return getInstance().getInternalMessageSender(configuration, properties);
    }

    MessageSender getInternalMessageSender(MessageSender.Configuration configuration, Properties properties)
    {
        if (!messageSenderMap.containsKey(configuration.connectionName()))
        {
            messageSenderMap.put(configuration.connectionName(), MessageSenderFactory.getMessageSender(MessagingManager.class, properties));
        }

        return messageSenderMap.get(configuration.connectionName());
    }

    static MessagingManager getInstance() {
        return messageSenderManager;
    }

}
