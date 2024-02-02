package io.jexxa.jlegmed.plugins.messaging.producer.jms;


import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.common.drivingadapter.messaging.jms.JMSAdapter;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.plugins.messaging.MessageConfiguration;

import java.util.function.BiFunction;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;

public class JMSProducer<T> extends ActiveProducer<T> {

    private IDrivingAdapter jmsAdapter;
    private final JMSListener<T> messageListener;

    public JMSProducer(MessageConfiguration messageConfiguration, BiFunction<String, Class<T>,T> deserializer) {
        this.messageListener = new JMSListener<>(messageConfiguration, deserializer);
    }

    @Override
    public void init() {
        super.init();
        if (properties().isEmpty()) {
            throw new IllegalArgumentException("PropertiesConfig is missing -> Configure properties of JMSProducer in your main");
        }

        this.jmsAdapter = new JMSAdapter(properties());

        messageListener.outputPipe(outputPipe());
        messageListener.typeInformation(producingType());

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



    @Override
    public IDrivingAdapter drivingAdapter() {
        return jmsAdapter;
    }

    public static <T> JMSProducer<T> jmsTopic(String topicName, BiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(topic(topicName), deserializer);
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, BiFunction<String, Class<T>, T> deserializer)
    {
        return new JMSProducer<>(queue(queueName), deserializer);
    }

}
