package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.flowgraph.Context;

import static io.jexxa.jlegmed.plugins.messaging.MessageProducer.DestinationType.QUEUE;
import static io.jexxa.jlegmed.plugins.messaging.MessageProducer.DestinationType.TOPIC;

public class MessageProcessors {
    public static Object sendAsJSON(Object content, Context context)
    {
        var messageConfiguration = context.getProcessorConfig(MessageConfiguration.class);
        var properties = context.getProperties(messageConfiguration.propertiesPrefix());

        var messageSender = MessageSenderManager.getMessageSender(MessageProcessors.class, properties);

        if ( messageConfiguration.destinationType.equals(TOPIC)) {
            messageSender.send(content)
                    .toTopic(messageConfiguration.destinationName)
                    .addHeader("Type", content.getClass().getSimpleName())
                    .asJson();
        } else {
            messageSender.send(content)
                    .toQueue(messageConfiguration.destinationName)
                    .addHeader("Type", content.getClass().getSimpleName())
                    .asJson();
        }

        return content;
    }

    private MessageProcessors()
    {

    }

    public record MessageConfiguration( MessageProducer.DestinationType destinationType, String destinationName, String propertiesPrefix) {
        public static MessageConfiguration topic(String topicName, String propertiesPrefix) {
            return new MessageConfiguration(TOPIC, topicName, propertiesPrefix);
        }
        public static MessageConfiguration queue(String queueName, String propertiesPrefix) {
            return new MessageConfiguration(QUEUE, queueName, propertiesPrefix);
        }
    }

}
