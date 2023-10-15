package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.flowgraph.Context;

import static io.jexxa.jlegmed.plugins.messaging.MessageProducer.DestinationType.TOPIC;

public class MessageProcessors {
    public static Object sendAsJSON(Object content, Context context)
    {
        var messageConfiguration = context.getProcessorConfig(MessageSender.Configuration.class);
        var properties = context.getProperties(messageConfiguration.connectionName());

        var messageSender = MessageSenderManager.getMessageSender(messageConfiguration, properties);

        if ( messageConfiguration.destinationType().equals(TOPIC)) {
            messageSender.send(content)
                    .toTopic(messageConfiguration.destinationName())
                    .addHeader("Type", content.getClass().getSimpleName())
                    .asJson();
        } else {
            messageSender.send(content)
                    .toQueue(messageConfiguration.destinationName())
                    .addHeader("Type", content.getClass().getSimpleName())
                    .asJson();
        }

        return content;
    }

    private MessageProcessors()
    {

    }

}
