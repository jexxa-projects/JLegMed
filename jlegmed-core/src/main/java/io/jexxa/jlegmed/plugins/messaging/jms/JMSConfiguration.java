package io.jexxa.jlegmed.plugins.messaging.jms;


import io.jexxa.common.drivenadapter.messaging.DestinationType;

import static io.jexxa.common.drivenadapter.messaging.DestinationType.QUEUE;
import static io.jexxa.common.drivenadapter.messaging.DestinationType.TOPIC;

public record JMSConfiguration(DestinationType destinationType, String destinationName)
{
    public static JMSConfiguration topic(String topicName) {
        return new JMSConfiguration(TOPIC, topicName);
    }
    public static JMSConfiguration queue(String queueName) {
        return new JMSConfiguration(QUEUE, queueName);
    }
}
