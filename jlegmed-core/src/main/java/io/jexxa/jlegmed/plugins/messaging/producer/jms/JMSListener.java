package io.jexxa.jlegmed.plugins.messaging.producer.jms;


import io.jexxa.common.drivingadapter.messaging.jms.DefaultJMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.function.BiFunction;

import static io.jexxa.common.drivenadapter.messaging.DestinationType.TOPIC;


public class JMSListener<T> extends JSONMessageListener {
    private OutputPipe<T> outputPipe;
    private Class<T> typeInformation;
    private final MessageConfiguration configuration;
    private final BiFunction<String, Class<T>, T> deserializer;

    protected JMSListener(MessageConfiguration configuration, BiFunction<String, Class<T>, T> deserializer) {
        this.configuration = configuration;
        this.deserializer = deserializer;
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
        if (configuration.destinationType() == TOPIC)
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
        onMessage(message, typeInformation, outputPipe);
    }

    protected void onMessage(String message, Class<T> typeInformation, OutputPipe<T> outputPipe)
    {
        outputPipe.forward(deserializer.apply(message, typeInformation));
    }
}
