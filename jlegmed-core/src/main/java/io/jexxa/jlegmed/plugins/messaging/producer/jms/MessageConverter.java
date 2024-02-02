package io.jexxa.jlegmed.plugins.messaging.producer.jms;

import io.jexxa.common.facade.json.JSONManager;

public class MessageConverter {
    public static <T>  T fromJSON(String message, Class<T> typeInformation)
    {
        return JSONManager
                .getJSONConverter()
                .fromJson(message, typeInformation);
    }

    private MessageConverter() {
        //Private constructor to hide the implicit public one
    }
}
