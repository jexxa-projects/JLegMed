package io.jexxa.jlegmed.plugins.messaging.jms;


import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.common.drivingadapter.messaging.jms.DefaultJMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.JMSAdapter;
import io.jexxa.common.drivingadapter.messaging.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;

import static io.jexxa.common.drivenadapter.messaging.DestinationType.TOPIC;

public class JMSProducer<T> extends ActiveProducer<T> {

    private IDrivingAdapter jmsAdapter;
    private final JMSListener<T> messageListener;

    public JMSProducer(JMSConfiguration jmsConfiguration, BiFunction<String, Class<T>,T> decoder) {
        this.messageListener = new JMSListener<>(jmsConfiguration, decoder);
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




    public static class JMSListener<T> extends JSONMessageListener {
        private OutputPipe<T> outputPipe;
        private Class<T> typeInformation;
        private final JMSConfiguration configuration;
        private final BiFunction<String, Class<T>, T> decoder;

        protected JMSListener(JMSConfiguration configuration, BiFunction<String, Class<T>, T> decoder) {
            this.configuration = configuration;
            this.decoder = decoder;
        }

        public void outputPipe(OutputPipe<T> outputPipe)
        {
            this.outputPipe = outputPipe;
        }

        public void typeInformation(Class<T> typeInformation)
        {
            this.typeInformation = typeInformation;
        }

        @SuppressWarnings("unused")
        public io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration getConfiguration()
        {
            io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration.MessagingType messagingType;
            if (configuration.destinationType() == TOPIC)
            {
                messagingType = io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration.MessagingType.TOPIC;
            } else
            {
                messagingType = io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration.MessagingType.QUEUE;
            }

            return new DefaultJMSConfiguration(configuration.destinationName(), messagingType);
        }

        @Override
        public void onMessage(String message) {
            onMessage(message, typeInformation, outputPipe);
        }

        protected void onMessage(String message, Class<T> typeInformation, OutputPipe<T> outputPipe)
        {
            outputPipe.forward(decoder.apply(message, typeInformation));
        }
    }
}
