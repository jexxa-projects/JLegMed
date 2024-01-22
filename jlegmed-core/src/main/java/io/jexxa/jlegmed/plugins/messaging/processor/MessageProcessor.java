package io.jexxa.jlegmed.plugins.messaging.processor;


import io.jexxa.common.drivenadapter.messaging.DestinationType;
import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

public abstract class MessageProcessor<T> extends Processor<T,T> {
    private MessageSender messageSender;
    private final MessageConfiguration messageConfiguration;

    protected MessageProcessor(MessageConfiguration messageConfiguration)
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
    protected T doProcess(T data, FilterContext context, OutputPipe<T> outputPipe) {
        doProcess(data, new MessageProcessorContext(context, messageSender, messageConfiguration));
        return data;
    }
    protected abstract void doProcess(T data, MessageProcessorContext context);


    public static <T> void asJSON(T data, MessageProcessor.MessageProcessorContext context)
    {
        var messageFactory = context.messageSender()
                .send(data)
                .addHeader("Type", data.getClass().getSimpleName());
        if (context.messageConfiguration().destinationType().equals(DestinationType.QUEUE))
        {
            messageFactory.toQueue(context.messageConfiguration().destinationName()).asJson();
        } else  {
            messageFactory.toTopic(context.messageConfiguration().destinationName()).asJson();
        }
    }


    public record MessageProcessorContext(FilterContext filterContext, MessageSender messageSender, MessageConfiguration messageConfiguration) {}

}
