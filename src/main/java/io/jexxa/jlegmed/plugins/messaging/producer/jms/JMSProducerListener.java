package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.jlegmed.common.component.messaging.receive.jms.DefaultJMSConfiguration;
import io.jexxa.jlegmed.common.component.messaging.receive.jms.JMSConfiguration;
import io.jexxa.jlegmed.common.component.messaging.receive.jms.listener.TypedMessageListener;
import io.jexxa.jlegmed.common.component.messaging.send.MessageFactory;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.Objects;

public class JMSProducerListener<T> extends TypedMessageListener<T> {
    private final OutputPipe<T> outputPipe;
    private final MessageConfiguration configuration;
    private final Context context;

    public JMSProducerListener(Class<T> clazz, OutputPipe<T> outputPipe, MessageConfiguration configuration, Context context) {
        super(clazz);
        this.outputPipe = Objects.requireNonNull(outputPipe);
        this.configuration = configuration;
        this.context = context;
    }

    @SuppressWarnings("unused")
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

    @Override
    protected void onMessage(T message) {
        outputPipe.forward(message, context);
    }
}
