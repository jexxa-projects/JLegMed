package io.jexxa.jlegmed.plugins.messaging.processor;

public class MessageCommands {
    private final String destination;
    public MessageCommands(String destination)
    {
        this.destination = destination;
    }
    public <T> void sendToTopicAsJSON(T data, MessageProcessor.MessageProcessorContext context)
    {
        context.messageSender()
                .send(data)
                .addHeader("Type", data.getClass().getSimpleName())
                .toTopic(destination).asJson();
    }

    public <T> void sendToQueueAsJSON(T data, MessageProcessor.MessageProcessorContext context)
    {
        context.messageSender()
                .send(data)
                .addHeader("Type", data.getClass().getSimpleName())
                .toQueue(destination).asJson();
    }
}
