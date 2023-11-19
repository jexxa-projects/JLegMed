package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.jlegmed.common.component.messaging.receive.jms.JMSAdapter;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiConsumer;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;

public class JMSProducer<T> extends ActiveProducer<T> {

    private IDrivingAdapter jmsAdapter;
    private final JMSProducerListener<T> messageListener;

    public JMSProducer(JMSProducerListener<T> messageListener)
    {
        this.messageListener = messageListener;
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

    public static <T> JMSProducer<T> jmsTopic(String topicName, BiConsumer<String, JMSProducer.JMSProducerContext<T>> consumer)
    {
        return new JMSProducer<>(new JMSProducerListener<>(topic(topicName)) {
            @Override
            public void onMessage(String message, JMSProducerContext<T> context) {
                consumer.accept(message, context);
            }
        });
    }

    public static <T> JMSProducer<T> jmsQueue(String queueName, BiConsumer<String, JMSProducer.JMSProducerContext<T>> consumer)
    {
        return new JMSProducer<>(new JMSProducerListener<>(queue(queueName)) {
            @Override
            public void onMessage(String message, JMSProducerContext<T> context) {
                consumer.accept(message, context);
            }
        });
    }
    public record JMSProducerContext<T>(Class<T> typeInformation, OutputPipe<T> outputPipe) {}
}
