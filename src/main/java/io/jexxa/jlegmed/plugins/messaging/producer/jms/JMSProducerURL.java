package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.jlegmed.core.flowgraph.AbstractFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.TypedConnector;
import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.producer.ActiveProducerURL;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducerURL<T> implements ActiveProducerURL<T> {

    private final MessageSender.Configuration configuration;
    private MessageProducer<T> messageProducer;
    private AbstractFlowGraph<T> flowGraph;

    public JMSProducerURL(MessageSender.Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public ActiveProducer init(AbstractFlowGraph<T> flowGraph)
    {
        messageProducer = new MessageProducer<>(configuration.connectionName(),
                flowGraph.getContext().getProperties(configuration.connectionName()));

        this.flowGraph = flowGraph;
        return messageProducer;
    }
    public TypedConnector<T> asJSON( )
    {
        JSONMessageListener messageListener = new TypedMessageListener<>(
                flowGraph.getInputData(),
                messageProducer.getOutputPipe(),
                configuration,
                flowGraph.getContext());
        messageProducer.register(messageListener);

        return new TypedConnector<>(messageProducer.getOutputPipe(), null);
    }
}
