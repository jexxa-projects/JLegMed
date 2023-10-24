package io.jexxa.jlegmed.plugins.messaging.producer.jms.listener;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageFactory;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.DefaultJMSConfiguration;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSConfiguration;

import java.util.Objects;

import static io.jexxa.jlegmed.common.json.JSONManager.getJSONConverter;


@SuppressWarnings("unused")
public class TypedMessageListener<T> extends JSONMessageListener
{
    private final Class<T> clazz;
    private final OutputPipe<T> outputPipe;
    private final MessageSender.Configuration configuration;
    private final Context context;

    public TypedMessageListener(Class<T> clazz, OutputPipe<T> outputPipe, MessageSender.Configuration configuration, Context context)
    {
        this.clazz = Objects.requireNonNull( clazz );
        this.outputPipe = Objects.requireNonNull(outputPipe);
        this.configuration = configuration;
        this.context = context;
    }

    public void forwardMessage(T message)
    {
        outputPipe.forward(message, context);
    }

    @Override
    public final void onMessage(String message)
    {
        forwardMessage( mfromJson(message, clazz ));
    }

    public JMSConfiguration getConfiguration()
    {
        JMSConfiguration.MessagingType messagingType;
        if (configuration.destinationType() == MessageFactory.DestinationType.TOPIC)
        {
            messagingType = JMSConfiguration.MessagingType.TOPIC;
        } else
        {
            messagingType = JMSConfiguration.MessagingType.QUEUE;
        }

        return new DefaultJMSConfiguration(configuration.destinationName(), messagingType);
    }


    protected T mfromJson( String message, Class<T> clazz)
    {
        return getJSONConverter().fromJson( message, clazz);
    }

}
