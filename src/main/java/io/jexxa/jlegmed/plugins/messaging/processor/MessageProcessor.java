package io.jexxa.jlegmed.plugins.messaging.processor;

import io.jexxa.jlegmed.common.component.messaging.send.MessageSender;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;

import java.util.Properties;
import java.util.function.BiConsumer;

public abstract class MessageProcessor<T> extends Processor<T,T> {
    private MessageSender messageSender;

    @Override
    public void init()
    {
        super.init();

        var properties = new Properties();
        var connectionName = "message-logger";
        if ( filterContext().filterProperties().isPresent())
        {
            properties = filterContext().filterProperties().orElseThrow().properties();
            connectionName = filterContext().filterProperties().orElseThrow().propertiesName();
        }

        messageSender = MessagingManager.getMessageSender(connectionName, properties);
    }

    @Override
    protected T doProcess(T data, FilterContext context) {
        doProcess(data, new MessageProcessorContext(context, messageSender));
        return data;
    }
    protected abstract void doProcess(T data, MessageProcessorContext context);

    public static <T> MessageProcessor<T> messageProcessor(BiConsumer<T, MessageProcessorContext> processFunction)
    {
        return new MessageProcessor<>() {
            @Override
            protected void doProcess(T data, MessageProcessorContext context) {
                processFunction.accept(data, context);
            }
        };
    }

    public record MessageProcessorContext(FilterContext filterContext, MessageSender messageSender) {}

}
