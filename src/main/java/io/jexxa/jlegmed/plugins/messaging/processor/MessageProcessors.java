package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.Properties;

import static io.jexxa.jlegmed.common.component.messaging.send.MessageFactory.DestinationType.TOPIC;

public class MessageProcessors {
    public static <T> T sendAsJSON(T content, FilterContext context)
    {
        var messageConfiguration = context.filterConfigAs(MessageConfiguration.class).orElseThrow();

        var properties = new Properties();
        var connectionName = "message-logger";
        if ( context.filterProperties().isPresent())
        {
            properties = context.filterProperties().orElseThrow().properties();
            connectionName = context.filterProperties().orElseThrow().propertiesName();
        }

        var messageSender = MessagingManager.getMessageSender(connectionName, properties);

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
