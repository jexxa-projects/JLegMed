package io.jexxa.jlegmed.plugins.messaging.producer.jms.listener;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
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
    private final FlowGraph flowGraph;
    private final MessageSender.Configuration configuration;

    public TypedMessageListener(Class<T> clazz, FlowGraph flowGraph, MessageSender.Configuration configuration)
    {
        this.clazz = Objects.requireNonNull( clazz );
        this.flowGraph = Objects.requireNonNull(flowGraph);
        this.configuration = configuration;
    }

    public void onMessage(T message)
    {
        flowGraph.processMessage(new Content(message));
    }

    @Override
    public final void onMessage(String message)
    {
        onMessage( fromJson(message, clazz ));
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


    protected static <U> U fromJson( String message, Class<U> clazz)
    {
        return getJSONConverter().fromJson( message, clazz);
    }

}
