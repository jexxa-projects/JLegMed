package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducer;
import io.jexxa.jlegmed.plugins.messaging.producer.jms.JMSProducerListener;

import java.util.function.BiConsumer;

import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.queue;
import static io.jexxa.jlegmed.plugins.messaging.MessageConfiguration.topic;

public class JMSFilter {

    public static <T> JMSProducer<T> topicReceiver(String topicName, BiConsumer<String, JMSProducer.JMSProducerContext<T>> consumer)
    {
        return new JMSProducer<>(new JMSProducerListener<>(topic(topicName)) {
            @Override
            public void onMessage(String message, JMSProducer.JMSProducerContext<T> context) {
                consumer.accept(message, context);
            }
        });
    }

    public static <T> JMSProducer<T> queueReceiver(String queueName, BiConsumer<String, JMSProducer.JMSProducerContext<T>> consumer)
    {
        return new JMSProducer<>(new JMSProducerListener<>(queue(queueName)) {
            @Override
            public void onMessage(String message, JMSProducer.JMSProducerContext<T> context) {
                consumer.accept(message, context);
            }
        });
    }


    private JMSFilter()
    {
        // Private constructor since we provide only static methods
    }
}
