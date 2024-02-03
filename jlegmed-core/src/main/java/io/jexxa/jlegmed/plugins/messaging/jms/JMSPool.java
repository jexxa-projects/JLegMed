package io.jexxa.jlegmed.plugins.messaging.jms;

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
import java.util.function.BiFunction;

import static io.jexxa.jlegmed.plugins.messaging.jms.JMSConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSConfiguration.topic;

@SuppressWarnings("java:S6548")
public class JMSPool {
    private static boolean initialized = false;
    private static final JMSPool INSTANCE = new JMSPool();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();

    public static void init()
    {
        initialized = true;
    }

    public static MessageSender jmsSender(FilterContext filterContext)
    {
        if (!initialized) {
            SLF4jLogger.getLogger(JDBCSessionPool.class).warn("MessageSender pool is not initialized. " +
                    "Please invoke MessageSender.init() in main");
        }

        INSTANCE.messageSenderMap.computeIfAbsent(filterContext.propertiesName(),
                key -> INSTANCE.internalJMSSender(filterContext.filterProperties()));

        return INSTANCE.messageSenderMap.get(filterContext.propertiesName());
    }

    public static <T> JMSProducer<T> jmsTopic(String topicName, BiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(topic(topicName), deserializer);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, BiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(queue(queueName), deserializer);
    }

    private void initJMSConnections(FilterProperties filterProperties)
    {
        if (filterProperties.properties().containsKey(JMSProperties.JNDI_FACTORY_KEY))
        {
            internalJMSSender(filterProperties);
        }
    }

    private MessageSender internalJMSSender(FilterProperties filterProperties)
    {
        return MessageSenderManager.getMessageSender(JMSPool.class, filterProperties.properties());
    }


    private JMSPool()
    {
        // Currently transactional outbox sender causes strange site effects at least in test.
        // One reason could be that it is designed as singleton. Therefore, we use JMSSender at the moment
        MessageSenderManager.setDefaultStrategy(JMSSender.class);
        BootstrapRegistry.registerFailFastHandler(properties -> messageSenderMap.clear());
        BootstrapRegistry.registerFailFastHandler(this::initJMSConnections);
    }
}
