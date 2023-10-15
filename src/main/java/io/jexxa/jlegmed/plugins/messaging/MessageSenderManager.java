package io.jexxa.jlegmed.plugins.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MessageSenderManager {
    private static final MessageSenderManager messageSenderManager = new MessageSenderManager();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();


    public static MessageSender getMessageSender(MessageSender.Configuration configuration, Properties properties)
    {
        return getInstance().getInternalMessageSender(configuration, properties);
    }

    MessageSender getInternalMessageSender(MessageSender.Configuration configuration, Properties properties)
    {
        if (!messageSenderMap.containsKey(configuration.connectionName()))
        {
            messageSenderMap.put(configuration.connectionName(), MessageSenderFactory.getMessageSender(MessageSenderManager.class, properties));
        }

        return messageSenderMap.get(configuration.connectionName());
    }
    static MessageSenderManager getInstance() {
        return messageSenderManager;
    }

}
