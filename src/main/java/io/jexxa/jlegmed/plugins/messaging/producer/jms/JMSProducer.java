package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.listener.TypedMessageListener;

public class JMSProducer<T> extends Producer<T> {

    private IDrivingAdapter jmsAdapter;

    @Override
    public void start() {
        var configuration = getFilterConfig(MessageConfiguration.class).orElseThrow(() -> new IllegalArgumentException("No MessageConfiguration configuration provided"));

        if (this.jmsAdapter == null)
        {
            var filterProperties = getFilterProperties()
                    .orElseThrow( () -> new IllegalArgumentException("PropertiesConfig is missing -> Configure properties of JMSProducer in your main"));

            this.jmsAdapter = new JMSAdapter(filterProperties);

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
        if (jmsAdapter != null)
        {
            jmsAdapter.stop();
        }
    }

    public static <T> JMSProducer<T> jmsJSONProducer()
    {
        return new JMSProducer<>();
    }

}
