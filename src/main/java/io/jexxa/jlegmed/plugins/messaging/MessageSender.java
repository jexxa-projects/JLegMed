package io.jexxa.jlegmed.plugins.messaging;


import io.jexxa.jlegmed.common.annotation.CheckReturnValue;

import java.util.Properties;

import static io.jexxa.jlegmed.plugins.messaging.MessageProducer.DestinationType.QUEUE;
import static io.jexxa.jlegmed.plugins.messaging.MessageProducer.DestinationType.TOPIC;

public abstract class MessageSender
{
    public enum MessageType{TEXT_MESSAGE, BYTE_MESSAGE }

    @CheckReturnValue
    public <T> MessageProducer send(T message)
    {
        return new MessageProducer(message, this, MessageType.TEXT_MESSAGE);
    }

    @CheckReturnValue
    public <T> MessageProducer sendByteMessage(T message)
    {
        return new MessageProducer(message, this, MessageType.BYTE_MESSAGE);
    }

    /**
     * Sends an asynchronous text message to a queue
     *
     * @param message message as string. Must not be null
     * @param destination name of the queue to send the message
     * @param messageProperties additional properties of the message. Can be null if no properties are required
     */
    protected abstract void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType);

    /**
     * Sends an asynchronous text message to a topic
     *
     * @param message message as string. Must not be null
     * @param destination name of the queue to send the message
     * @param messageProperties additional properties of the message. Can be null if no properties are required
     */
    protected abstract void sendToTopic(String message, String destination, Properties messageProperties, MessageType messageType);

    public record Configuration(MessageProducer.DestinationType destinationType, String destinationName, String connectionName) {
        public static Configuration topic(String topicName, String connectionName) {
            return new Configuration(TOPIC, topicName, connectionName);
        }
        public static Configuration queue(String queueName, String connectionName) {
            return new Configuration(QUEUE, queueName, connectionName);
        }
    }
}
