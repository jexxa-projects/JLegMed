package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.core.filter.Binding;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducer<T> extends TypedProducer<T> {

    private final MessageSender.Configuration configuration;
    private IDrivingAdapter jmsAdapter;

    public JMSProducer(MessageSender.Configuration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public void start() {
        jmsAdapter.start();
    }

    @Override
    public void stop() {
        jmsAdapter.stop();
    }

    public Binding<T> asJSON( )
    {
        this.jmsAdapter = new JMSAdapter(getContext().getProperties(configuration.connectionName()));

        JSONMessageListener messageListener = new TypedMessageListener<>(
                getType(),
                getOutputPipe(),
                configuration,
                getContext());
        jmsAdapter.register(messageListener);

        return new Binding<>(getOutputPipe(), null);
    }
}
