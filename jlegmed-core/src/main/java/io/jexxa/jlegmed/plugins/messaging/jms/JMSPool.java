package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.facade.jms.JMSProperties;
import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.createMessageSender;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSSource.queue;
import static io.jexxa.jlegmed.plugins.messaging.jms.JMSSource.topic;

@SuppressWarnings("java:S6548")
public class JMSPool {
    private static final JMSPool INSTANCE = new JMSPool();

    private final Map<FilterContext, MessageSender> messageSenderMap = new ConcurrentHashMap<>();

    public static MessageSender jmsSender(FilterContext filterContext)
    {
        return INSTANCE.internalJMSSender(filterContext);
    }

    public static <T> JMSProducer<T> jmsSource(JMSSource jmsSource, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(jmsSource, deserializer);
    }


    public static <T> JMSProducer<T> jmsTopic(String topicName, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(topic(topicName), deserializer);
    }

    public static <T> JMSProducer<T> jmsTopic(String topicName, String selector, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(topic(topicName, selector), deserializer);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(queue(queueName), deserializer);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, String selector, SerializableBiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(queue(queueName, selector), deserializer);
    }

    private void initJMSConnections(FilterProperties filterProperties)
    {
        try {
            if (filterProperties.properties().containsKey(JMSProperties.JNDI_FACTORY_KEY))
            {
                createMessageSender(JMSPool.class, filterProperties.properties());
            }
        } catch ( RuntimeException e) {
            throw new FailFastException("Could not init JMS connection for filter properties " + filterProperties.name()
                    + ". Reason: " + e.getMessage(), e );
        }

    }

    private MessageSender internalJMSSender(FilterContext filterContext)
    {
        INSTANCE.messageSenderMap.computeIfAbsent(filterContext,
                key ->  createMessageSender(JMSPool.class, filterContext.properties()));

        return messageSenderMap.get(filterContext);
    }


    private JMSPool()
    {
        BootstrapRegistry.registerFailFastHandler(properties -> messageSenderMap.clear());
        BootstrapRegistry.registerFailFastHandler(this::initJMSConnections);
    }
}
