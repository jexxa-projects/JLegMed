package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MessageProducer implements ActiveProducer {

    private static final Map<String, IDrivingAdapter> messageReceiverMap = new HashMap<>();

    private IDrivingAdapter jmsAdapter;
    @Override
    public void init(Properties properties, FlowGraph flowGraph) {
        jmsAdapter = getInternalMessageReceiver("MyConfig",  properties);
    }

    @Override
    public void start() {
        jmsAdapter.start();
    }

    @Override
    public void stop() {
        jmsAdapter.stop();
    }

    public void register(MessageListener messageListener)
    {
        jmsAdapter.register(messageListener);
    }


    static IDrivingAdapter getInternalMessageReceiver(String connectionName, Properties properties)
    {
        messageReceiverMap.computeIfAbsent(connectionName, key -> new JMSAdapter(properties));
        return messageReceiverMap.get(connectionName);
    }
}
