package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.jlegmed.core.flowgraph.ActiveFlowGraph;
import io.jexxa.jlegmed.core.producer.ActiveProducerURL;
import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducerURL implements ActiveProducerURL {

    private final MessageSender.Configuration configuration;
    private MessageProducer messageProducer;
    private ActiveFlowGraph<?> flowGraph;

    public JMSProducerURL(MessageSender.Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public <T> ActiveProducer init(ActiveFlowGraph<T> flowGraph)
    {
        messageProducer = new MessageProducer(configuration.connectionName(),
                flowGraph.getContext().getProperties(configuration.connectionName()));

        this.flowGraph = flowGraph;
        return messageProducer;
    }
    public FlowGraph asJSON( )
    {
        JSONMessageListener messageListener = new TypedMessageListener<>(flowGraph.getInputDataType(), flowGraph, configuration);
        messageProducer.register(messageListener);

        return flowGraph;
    }
}
