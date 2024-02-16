package io.jexxa.jlegmed.plugins.messaging.jms;


import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.drivingadapter.messaging.jms.DefaultJMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.JMSAdapter;
import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.listener.JSONMessageListener;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.function.BiFunction;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

public class JMSProducer<T> extends ActiveProducer<T> {

    private IDrivingAdapter jmsAdapter;
    private final JMSListener<T> messageListener;
    private final String name;

    public JMSProducer(JMSSource jmsSource, SerializableBiFunction<String, Class<T>, T> decoder) {
        this.name = JMSProducer.class.getSimpleName() + ":" + methodNameFromLambda(decoder);
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
        private final JMSSource configuration;
        private final BiFunction<String, Class<T>, T> decoder;

        protected JMSListener(JMSSource configuration, BiFunction<String, Class<T>, T> decoder) {
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
        public JMSConfiguration getConfiguration()
        {
            return switch (configuration.destinationType()) {
                case TOPIC ->
                        new DefaultJMSConfiguration(configuration.destinationName(), JMSConfiguration.MessagingType.TOPIC);
                case QUEUE ->
                        new DefaultJMSConfiguration(configuration.destinationName(), JMSConfiguration.MessagingType.QUEUE);
            };
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
