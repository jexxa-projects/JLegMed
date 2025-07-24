package io.jexxa.jlegmed.plugins.messaging.jms;


import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.drivingadapter.messaging.jms.JMSAdapter;
import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.listener.StringMessageListener;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;

import java.util.function.BiFunction;

public class JMSProducer<T> extends ActiveProducer<T> {

    private IDrivingAdapter jmsAdapter;
    private final JMSListener<T> messageListener;
    private final String name;

    public JMSProducer(JMSSource jmsSource, SerializableBiFunction<String, Class<T>, T> decoder) {
        this.name = JMSProducer.class.getSimpleName() + ":" + filterNameFromLambda(decoder);
        this.messageListener = new JMSListener<>(jmsSource, decoder);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void init() {
        super.init();
        if (properties().isEmpty()) {
            throw new IllegalArgumentException("PropertiesConfig is missing -> Configure properties of JMSProducer in your main");
        }

        this.jmsAdapter = new JMSAdapter(properties());

        messageListener.jmsProducer(this);
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




    public static class JMSListener<T> extends StringMessageListener {
        private JMSProducer<T> jmsProducer;
        private Class<T> typeInformation;
        private final JMSSource configuration;
        private final BiFunction<String, Class<T>, T> decoder;

        protected JMSListener(JMSSource configuration, BiFunction<String, Class<T>, T> decoder) {
            this.configuration = configuration;
            this.decoder = decoder;
        }

        public void jmsProducer(JMSProducer<T> jmsProducer)
        {
            this.jmsProducer = jmsProducer;
        }

        public void typeInformation(Class<T> typeInformation)
        {
            this.typeInformation = typeInformation;
        }

        @SuppressWarnings("unused")
        public JMSConfiguration getConfiguration()
        {
            return configuration;
        }

        @Override
        public void onMessage(String message) {
            T decodedMessage = null;
            try {
                decodedMessage = decoder.apply(message, typeInformation);
                jmsProducer.outputPipe().forward(decodedMessage);
            } catch (ProcessingException e) {
                jmsProducer.errorPipe().forward(new ProcessingError<>(decodedMessage, e));
            } catch (RuntimeException e) {
                jmsProducer.errorPipe().forward(new ProcessingError<>(decodedMessage, new ProcessingException(jmsProducer.name(), "Could not deserialize message", e)));
            }
        }
    }
}
