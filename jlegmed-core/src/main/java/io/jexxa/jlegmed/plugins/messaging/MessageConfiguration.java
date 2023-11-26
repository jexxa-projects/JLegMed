package io.jexxa.jlegmed.plugins.messaging;


import io.jexxa.common.adapter.messaging.DestinationType;

import static io.jexxa.common.adapter.messaging.DestinationType.QUEUE;
import static io.jexxa.common.adapter.messaging.DestinationType.TOPIC;

public record MessageConfiguration(DestinationType destinationType, String destinationName)
{
    public static MessageConfiguration topic(String topicName) {
        return new MessageConfiguration(TOPIC, topicName);
    }
    public static MessageConfiguration queue(String queueName) {
        return new MessageConfiguration(QUEUE, queueName);
    }
}
