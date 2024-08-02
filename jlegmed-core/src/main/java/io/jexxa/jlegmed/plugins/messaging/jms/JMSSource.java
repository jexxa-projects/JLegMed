package io.jexxa.jlegmed.plugins.messaging.jms;


import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;

import java.lang.annotation.Annotation;

import static io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration.MessagingType.QUEUE;
import static io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration.MessagingType.TOPIC;


@SuppressWarnings("ClassExplicitlyAnnotation")
public record JMSSource(MessagingType messagingType,
                        String destination,
                        String selector,
                        DurableType durable,
                        String sharedSubscriptionName) implements JMSConfiguration
{
    @Override
    public Class<? extends Annotation> annotationType() {
        return JMSConfiguration.class;
    }

    public static JMSSource topic(String topicName) {
        return new JMSSource(TOPIC, topicName, "", DurableType.NON_DURABLE, "");
    }

    public static JMSSource topic(String topicName, String selector) {
        return new JMSSource(TOPIC, topicName, selector, DurableType.NON_DURABLE, "");
    }

    public static JMSSource queue(String queueName) {
        return new JMSSource(QUEUE, queueName, "", DurableType.NON_DURABLE, "");
    }

    public static JMSSource queue(String queueName, String selector) {
        return new JMSSource(QUEUE, queueName, selector, DurableType.NON_DURABLE, "");
    }
}
