package io.jexxa.jlegmed.plugins.messaging.processor;



import io.jexxa.commons.wrapper.component.messaging.send.MessageFactory;
import io.jexxa.commons.wrapper.component.messaging.send.MessageSender;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.function.BiConsumer;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;

public abstract class MessageProcessor<T> extends Processor<T,T> {
    private MessageSender messageSender;
    private final MessageConfiguration messageConfiguration;

    MessageProcessor(MessageConfiguration messageConfiguration)
    {
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public void init()
    {
        super.init();

        var connectionName = "";

        if (!propertiesName().isEmpty()) {
            connectionName = propertiesName();
        } else {
            connectionName = "message-logger";
        }


        messageSender = MessagingManager.getMessageSender(connectionName, properties());
    }

    @Override
    protected T doProcess(T data, FilterContext context) {
        doProcess(data, new MessageProcessorContext(context, messageSender, messageConfiguration));
        return data;
    }
    protected abstract void doProcess(T data, MessageProcessorContext context);


    public static <T> MessageProcessor<T> sendToTopic(String topicName, BiConsumer<T, MessageProcessorContext> processFunction)
    {
        return new MessageProcessor<>(topic(topicName)) {
            @Override
            protected void doProcess(T data, MessageProcessorContext context) {
                processFunction.accept(data, context);
            }
        };
    }

    public static <T> MessageProcessor<T> sendToQueue(String queueName, BiConsumer<T, MessageProcessorContext> processFunction)
    {
        return new MessageProcessor<>(queue(queueName)) {
            @Override
            protected void doProcess(T data, MessageProcessorContext context) {
                processFunction.accept(data, context);
            }
        };
    }

    public static <T> void asJSON(T data, MessageProcessor.MessageProcessorContext context)
    {
        var messageFactory = context.messageSender()
                .send(data)
                .addHeader("Type", data.getClass().getSimpleName());
        if (context.messageConfiguration().destinationType().equals(MessageFactory.DestinationType.QUEUE))
        {
            messageFactory.toQueue(context.messageConfiguration().destinationName()).asJson();
        } else  {
            messageFactory.toTopic(context.messageConfiguration().destinationName()).asJson();
        }
    }

    public record MessageProcessorContext(FilterContext filterContext, MessageSender messageSender, MessageConfiguration messageConfiguration) {}

}
