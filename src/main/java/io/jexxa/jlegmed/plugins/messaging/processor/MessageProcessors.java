package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.jlegmed.core.filter.Context;

import static io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory.DestinationType.TOPIC;

public class MessageProcessors {
    public static <T> T sendAsJSON(T content, Context context)
    {
        var messageConfiguration = context.getProcessorConfig(MessageSender.Configuration.class);
        var properties = context.getProperties(messageConfiguration.connectionName());

        var messageSender = MessagingManager.getMessageSender(messageConfiguration, properties);

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
