package io.jexxa.jlegmed.plugins.messaging;


import io.jexxa.commons.wrapper.component.messaging.send.MessageFactory;

import static io.jexxa.commons.wrapper.component.messaging.send.MessageFactory.DestinationType.QUEUE;
import static io.jexxa.commons.wrapper.component.messaging.send.MessageFactory.DestinationType.TOPIC;

public record MessageConfiguration(MessageFactory.DestinationType destinationType, String destinationName)
{
    public static MessageConfiguration topic(String topicName) {
        return new MessageConfiguration(TOPIC, topicName);
    }
    public static MessageConfiguration queue(String queueName) {
        return new MessageConfiguration(QUEUE, queueName);
    }
}
