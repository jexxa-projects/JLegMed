package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.Context;
import io.jexxa.jlegmed.core.Content;

public class MessageProcessors {
    public static Content sendToTopicAsJSON(Content content, Context context, String topic)
    {
        var messageSender = MessageSenderManager.getMessageSender(MessageProcessors.class, context.getProperties());
        messageSender.send(content.data())
                .toTopic(topic)
                .addHeader("Type", content.data().getClass().getSimpleName())
                .asJson();

        return content;
    }

    private MessageProcessors()
    {

    }
}
