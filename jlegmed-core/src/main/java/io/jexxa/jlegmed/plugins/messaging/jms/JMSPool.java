package io.jexxa.jlegmed.plugins.messaging.jms;

import io.jexxa.adapterapi.ConfigurationFailedException;
import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.facade.jms.JMSProperties;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Map;
import java.util.Properties;
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

    public static <T> JMSProducer<T> jmsSource(JMSSource jmsSource, SerializableBiFunction<String, Class<T>, T> deserializer, Class<?> parentClass)
    {
        return new JMSProducer<>(jmsSource, deserializer,  parentClass);
    }


    public static <T> JMSProducer<T> jmsTopic(String topicName, SerializableBiFunction<String, Class<T>, T> deserializer, Class<?> parentClass)
    {
        return new JMSProducer<>(topic(topicName), deserializer, parentClass);
    }

    public static <T> JMSProducer<T> jmsTopic(String topicName, String selector, SerializableBiFunction<String, Class<T>, T> deserializer, Class<?> parentClass)
    {
        return new JMSProducer<>(topic(topicName, selector), deserializer, parentClass);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, SerializableBiFunction<String, Class<T>, T> deserializer, Class<?> parentClass)
    {
        return new JMSProducer<>(queue(queueName), deserializer, parentClass);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, String selector, SerializableBiFunction<String, Class<T>, T> deserializer, Class<?> parentClass)
    {
        return new JMSProducer<>(queue(queueName, selector), deserializer, parentClass);
    }

    private void initJMSConnections(Properties properties)
    {
        try {
            if (properties.containsKey(JMSProperties.jndiFactoryKey()))
            {
                createMessageSender(JMSPool.class, properties);
            }
        } catch ( RuntimeException e) {
            throw new ConfigurationFailedException("Could not init JMS connection for filter properties "
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
        JexxaContext.registerCleanupHandler(messageSenderMap::clear);
        JexxaContext.registerValidationHandler(this::initJMSConnections);
    }
}
