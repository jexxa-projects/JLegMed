package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.Context;
import io.jexxa.jlegmed.core.Message;

public class MessageProcessors {
    public static Message sendToTopicAsJSON(Message message, Context context, String topic)
    {
        var messageSender = MessageSenderManager.getMessageSender(MessageProcessors.class, context.getProperties());
        messageSender.send(message.getData())
                .toTopic(topic)
                .addHeader("Type", message.getData().getClass().getSimpleName())
                .asJson();

        return message;
    }

    private MessageProcessors()
    {

    }
}
