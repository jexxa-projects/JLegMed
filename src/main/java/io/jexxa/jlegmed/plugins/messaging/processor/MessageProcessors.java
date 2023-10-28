package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.Properties;

import static io.jexxa.jlegmed.common.component.messaging.send.MessageFactory.DestinationType.TOPIC;

public class MessageProcessors {
    public static <T> T sendAsJSON(T content, Context context)
    {
        var messageConfiguration = context.getFilterContext().getConfig(MessageConfiguration.class);
        var properties = new Properties();
        var connectionName = "message-logger";
        if ( context.getFilterContext().filterProperties().isPresent())
        {
            properties = context.getFilterContext().filterProperties().orElseThrow().properties();
            connectionName = context.getFilterContext().filterProperties().orElseThrow().configName();
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
