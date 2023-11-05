package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.common.component.messaging.receive.jms.JMSAdapter;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;

public class JMSProducer<T> extends Producer<T> {

    private IDrivingAdapter jmsAdapter;
    private final MessageConfiguration messsageConfiguration;

    public JMSProducer(MessageConfiguration messageConfiguration)
    {
        this.messsageConfiguration = messageConfiguration;
    }

    @Override
    public void init() {
        super.init();
        var properties = filterProperties()
                .orElseThrow(() -> new IllegalArgumentException("PropertiesConfig is missing -> Configure properties of JMSProducer in your main"))
                .properties();


        this.jmsAdapter = new JMSAdapter(properties);

        var messageListener = new JMSProducerListener<>(
                producingType(),
                outputPipe(),
                messsageConfiguration);
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

    public static <T> JMSProducer<T> jmsTopicAsJSON(String topicName)
    {
        return new JMSProducer<>(topic(topicName));
    }

}
