package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.jlegmed.common.wrapper.json.JSONManager;

public class JMSListener {
    public static <T>  void asJSON(String message, JMSProducer.JMSProducerContext<T> context)
    {
        context.outputPipe().forward(
                JSONManager
                        .getJSONConverter()
                        .fromJson(message, context.typeInformation()
                        )
            );
    }

    private JMSListener() {
        //Private constructor to hide the implicit public one
    }
}
