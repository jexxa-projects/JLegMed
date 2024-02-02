package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.drivenadapter.messaging.MessageSenderManager;
import io.jexxa.common.drivenadapter.messaging.jms.JMSSender;
import io.jexxa.common.facade.jms.JMSProperties;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.plugins.persistence.jdbc.JDBCSessionPool;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S6548")
public class MessageSenderPool {
    private static boolean initialized = false;
    private static final MessageSenderPool INSTANCE = new MessageSenderPool();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();

    public static void init()
    {
        initialized = true;
    }

    public static MessageSender getMessageSender(FilterContext filterContext)
    {
        if (!initialized) {
            SLF4jLogger.getLogger(JDBCSessionPool.class).warn("MessageSender pool is not initialized. " +
                    "Please invoke MessageSender.init() in main");
        }

        INSTANCE.messageSenderMap.computeIfAbsent(filterContext.propertiesName(),
                key -> INSTANCE.getInternalMessageSender(filterContext.filterProperties()));

        return INSTANCE.messageSenderMap.get(filterContext.propertiesName());
    }

    private void initMessageSender(FilterProperties filterProperties)
    {
        if (filterProperties.properties().containsKey(JMSProperties.JNDI_FACTORY_KEY))
        {
            getInternalMessageSender(filterProperties);
        }
    }

    private MessageSender getInternalMessageSender(FilterProperties filterProperties)
    {
        return MessageSenderManager.getMessageSender(MessageSenderPool.class, filterProperties.properties());
    }


    private MessageSenderPool()
    {
        // Currently transactional outbox sender causes strange site effects at least in test.
        // One reason could be that it is designed as singleton. Therefore, we use JMSSender at the moment
        MessageSenderManager.setDefaultStrategy(JMSSender.class);
        BootstrapRegistry.registerFailFastHandler(properties -> messageSenderMap.clear());
        BootstrapRegistry.registerFailFastHandler(this::initMessageSender);
    }
}
