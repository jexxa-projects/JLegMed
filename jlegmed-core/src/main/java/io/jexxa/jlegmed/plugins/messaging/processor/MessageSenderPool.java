package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.drivenadapter.messaging.MessageSenderManager;
import io.jexxa.common.drivenadapter.messaging.jms.JMSSender;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("java:S6548")
public class MessageSenderPool {
    private static final MessageSenderPool MESSAGE_SENDER_POOL = new MessageSenderPool();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();


    public static MessageSender getMessageSender(String connectionName, Properties properties)
    {
        return  getInstance().getInternalMessageSender(connectionName, properties);
    }

    public static MessageSender getMessageSender(FilterContext filterContext)
    {
        return getMessageSender(filterContext.propertiesName(), filterContext.properties());
    }

    MessageSender getInternalMessageSender(String connectionName, Properties properties)
    {
        messageSenderMap.computeIfAbsent(connectionName,
                key -> MessageSenderManager.getMessageSender(MessageSenderPool.class, properties)
        );
        return messageSenderMap.get(connectionName);
    }

    static MessageSenderPool getInstance() {
        return MESSAGE_SENDER_POOL;
    }

    private MessageSenderPool()
    {
        // Currently transactional outbox sender causes strange site effects at least in test.
        // One reason could be that it is designed as singleton. Therefore, we use JMSSender at the moment
        MessageSenderManager.setDefaultStrategy(JMSSender.class);
        BootstrapRegistry.registerFailFastHandler(properties -> messageSenderMap.clear());
    }
}
