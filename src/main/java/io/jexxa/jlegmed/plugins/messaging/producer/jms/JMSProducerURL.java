package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.jlegmed.core.filter.Binding;
import io.jexxa.jlegmed.core.flowgraph.SourceConnector;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducerURL;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducerURL<T> implements ActiveProducerURL<T> {

    private final MessageSender.Configuration configuration;
    private MessageProducer<T> messageProducer;

    private SourceConnector<T> sourceConnector;

    public JMSProducerURL(MessageSender.Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public Producer<T> init(SourceConnector<T> sourceConnector)
    {
        messageProducer = new MessageProducer<>(configuration.connectionName(),
                sourceConnector.getContext().getProperties(configuration.connectionName()));

        this.sourceConnector = sourceConnector;
        return messageProducer;
    }
    public Binding<T> asJSON( )
    {
        JSONMessageListener messageListener = new TypedMessageListener<>(
                sourceConnector.getSourceType(),
                messageProducer.getOutputPipe(),
                configuration,
                sourceConnector.getContext());
        messageProducer.register(messageListener);

        return new Binding<>(messageProducer.getOutputPipe(), null);
    }
}
