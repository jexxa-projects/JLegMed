package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.Properties;

import static io.jexxa.jlegmed.core.filter.PropertiesConfig.properties;
import static io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory.DestinationType.TOPIC;

public class MessageProcessors {
    public static <T> T sendAsJSON(T content, Context context)
    {
        var messageConfiguration = context.getFilterConfig(MessageConfiguration.class);
        var properties = context.getFilterProperties().orElse(new Properties()); // Empty properties is a valid configuration

        var connectionName = context.getPropertiesConfig().orElse(properties("unnamed")).properties();

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
