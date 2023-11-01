package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.common.component.messaging.receive.jms.JMSAdapter;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

public class JMSProducer<T> extends Producer<T> {

    private IDrivingAdapter jmsAdapter;

    @Override
    public void init() {
        super.init();
        var configuration = filterConfigAs(MessageConfiguration.class).orElseThrow(() -> new IllegalArgumentException("No MessageConfiguration configuration provided"));
        var properties = filterProperties()
                .orElseThrow(() -> new IllegalArgumentException("PropertiesConfig is missing -> Configure properties of JMSProducer in your main"))
                .properties();


        this.jmsAdapter = new JMSAdapter(properties);

        var messageListener = new JMSProducerListener<>(
                producingType(),
                outputPipe(),
                configuration);
        jmsAdapter.register(messageListener);
    }
    @Override
    public void start() {
        jmsAdapter.start();
    }

    @Override
    public void stop() {
        if (jmsAdapter != null)
        {
            jmsAdapter.stop();
        }
    }

    @Override
    public void deInit()
    {
        jmsAdapter = null;
    }

    public static <T> JMSProducer<T> jmsJSONProducer()
    {
        return new JMSProducer<>();
    }

}
