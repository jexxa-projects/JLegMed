package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.producer.TypedProducer;
import io.jexxa.jlegmed.plugins.messaging.processor.MessageSender;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducer<T> extends TypedProducer<T> {

    private IDrivingAdapter jmsAdapter;


    @Override
    public void start() {
        var configuration = getFilterConfig(MessageSender.Configuration.class).orElseThrow(() -> new IllegalArgumentException("No messager configuration provided"));
        if (this.jmsAdapter == null)
        {
          this.jmsAdapter = new JMSAdapter(getContext().getProperties(configuration.connectionName()));

            JSONMessageListener messageListener = new TypedMessageListener<>(
                    getType(),
                    getOutputPipe(),
                    configuration,
                    getContext());
            jmsAdapter.register(messageListener);
        }
        jmsAdapter.start();
    }

    @Override
    public void stop() {
        jmsAdapter.stop();
    }

    @Override
    protected T doProduce(Context context) {
        return null;
    }
    public static <T> JMSProducer<T> receiveAsJSON()
    {
        return new JMSProducer<>();
    }

}
