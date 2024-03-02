package io.jexxa.jlegmed.plugins.messaging.jms;


import io.jexxa.common.drivenadapter.messaging.DestinationType;

import static io.jexxa.common.drivenadapter.messaging.DestinationType.QUEUE;
import static io.jexxa.common.drivenadapter.messaging.DestinationType.TOPIC;

public record JMSSource(DestinationType destinationType, String destinationName)
{
    public static JMSSource topic(String topicName) {
        return new JMSSource(TOPIC, topicName);
    }
    public static JMSSource queue(String queueName) {
        return new JMSSource(QUEUE, queueName);
    }
}
