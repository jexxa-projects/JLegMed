package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.jlegmed.core.flowgraph.ActiveFlowGraph;
import io.jexxa.jlegmed.core.producer.ActiveProducerURL;
import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducerURL extends ActiveProducerURL {

    private final MessageSender.Configuration configuration;
    private final MessageProducer messageProducer = new MessageProducer();

    public JMSProducerURL(MessageSender.Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public <T> void init(ActiveFlowGraph<T> flowGraph)
    {
        JSONMessageListener messageListener = new TypedMessageListener<>(flowGraph.getInputDataType(), flowGraph, configuration);
        getActiveProducer().init(flowGraph.getContext().getProperties(configuration.connectionName()), flowGraph);
        messageProducer.register(messageListener);
    }
    public FlowGraph asJSON( )
    {
        return getFlowgraph();
    }

    @Override
    public ActiveProducer getActiveProducer() {
        return messageProducer;
    }
}
