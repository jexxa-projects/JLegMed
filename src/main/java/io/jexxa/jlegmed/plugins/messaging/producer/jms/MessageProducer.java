package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.core.processor.OutputPipe;
import io.jexxa.jlegmed.core.producer.Producer;

import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MessageProducer<T> implements Producer<T> {

    private static final Map<String, IDrivingAdapter> messageReceiverMap = new HashMap<>();

    private final IDrivingAdapter jmsAdapter;
    private final OutputPipe<T> outputPipe = new OutputPipe<>();

    public MessageProducer(String connectionName, Properties properties)
    {
        jmsAdapter = getInternalMessageReceiver(connectionName, properties);
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

    @Override
    public OutputPipe<T> getOutputPipe()
    {
        return outputPipe;
    }

    static IDrivingAdapter getInternalMessageReceiver(String connectionName, Properties properties)
    {
        messageReceiverMap.computeIfAbsent(connectionName, key -> new JMSAdapter(properties));
        return messageReceiverMap.get(connectionName);
    }
}
