package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.common.facade.json.JSONManager;

public class MessageDecoder {
    public static <T>  T fromJSON(String message, Class<T> typeInformation)
    {
        return JSONManager
                .getJSONConverter()
                .fromJson(message, typeInformation);
    }

    private MessageDecoder() {
        //Private constructor to hide the implicit public one
    }
}
