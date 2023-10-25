package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory.DestinationType.QUEUE;
import static io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory.DestinationType.TOPIC;

public record MessageConfiguration(MessageFactory.DestinationType destinationType, String destinationName)
{
    public static MessageConfiguration topic(String topicName) {
        return new MessageConfiguration(TOPIC, topicName);
    }
    public static MessageConfiguration queue(String queueName) {
        return new MessageConfiguration(QUEUE, queueName);
    }
}
