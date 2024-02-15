package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.drivenadapter.messaging.MessageSenderManager;
import io.jexxa.common.drivenadapter.messaging.jms.JMSSender;
import io.jexxa.common.facade.jms.JMSProperties;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.HashMap;
import java.util.Map;

import static io.jexxa.jlegmed.plugins.messaging.jms.JMSConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSConfiguration.topic;

@SuppressWarnings("java:S6548")
public class JMSPool {
    private static final JMSPool INSTANCE = new JMSPool();

    private final Map<String, MessageSender> messageSenderMap = new HashMap<>();

    public static MessageSender jmsSender(FilterContext filterContext)
    {
        return INSTANCE.internalJMSSender(filterContext.filterProperties());
    }

    public static <T> JMSProducer<T> jmsTopic(String topicName, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(topic(topicName), deserializer);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(queue(queueName), deserializer);
    }

    private void initJMSConnections(FilterProperties filterProperties)
    {
        try {
            if (filterProperties.properties().containsKey(JMSProperties.JNDI_FACTORY_KEY))
            {
                internalJMSSender(filterProperties);
            }
        } catch ( RuntimeException e) {
            throw new FailFastException("Could not init JMS connection for filter properties " + filterProperties.name()
                    + ". Reason: " + e.getMessage(), e );
        }

    }

    private MessageSender internalJMSSender(FilterProperties filterProperties)
    {
        INSTANCE.messageSenderMap.computeIfAbsent(filterProperties.name(),
                key -> MessageSenderManager.getMessageSender(JMSPool.class, filterProperties.properties()));

        return messageSenderMap.get(filterProperties.name());
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
