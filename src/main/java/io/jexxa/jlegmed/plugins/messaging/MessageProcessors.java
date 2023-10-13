package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.flowgraph.Context;

public class MessageProcessors {
    public static Object sendToTopicAsJSON(Object content, Context context, String topic)
    {
        var messageSender = MessageSenderManager.getMessageSender(MessageProcessors.class, context.getProperties());
        messageSender.send(content)
                .toTopic(topic)
                .addHeader("Type", content.getClass().getSimpleName())
                .asJson();

        return content;
    }

    private MessageProcessors()
    {

    }
}
