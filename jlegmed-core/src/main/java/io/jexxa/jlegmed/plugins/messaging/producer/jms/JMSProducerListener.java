package io.jexxa.jlegmed.plugins.messaging.producer.jms;



import io.jexxa.commons.component.messaging.receive.jms.DefaultJMSConfiguration;
import io.jexxa.commons.component.messaging.receive.jms.JMSConfiguration;
import io.jexxa.commons.component.messaging.receive.jms.listener.JSONMessageListener;
import io.jexxa.commons.component.messaging.send.MessageFactory;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

public abstract class JMSProducerListener<T> extends JSONMessageListener {
    private OutputPipe<T> outputPipe;
    private Class<T> typeInformation;
    private final MessageConfiguration configuration;

    protected JMSProducerListener(MessageConfiguration configuration) {
        this.configuration = configuration;
    }

    public void outputPipe(OutputPipe<T> outputPipe)
    {
        this.outputPipe = outputPipe;
    }

    public void typeInformation(Class<T> typeInformation)
    {
        this.typeInformation = typeInformation;
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
    public void onMessage(String message) {
        onMessage(message, new JMSProducer.JMSProducerContext<>(typeInformation, outputPipe));
    }

    public abstract void onMessage(String message, JMSProducer.JMSProducerContext<T> context);
}
