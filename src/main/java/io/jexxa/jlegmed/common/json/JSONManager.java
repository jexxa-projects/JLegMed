package io.jexxa.jlegmed.common.json;

import io.jexxa.jlegmed.common.json.gson.GsonConverter;

@SuppressWarnings("unused")
public final class JSONManager
{
    private static JSONConverter jsonConverter = new GsonConverter();

    public static JSONConverter getJSONConverter()
    {
        return jsonConverter;
    }

    public static void setJSONConverter(JSONConverter jsonConverter)
    {
        JSONManager.jsonConverter = jsonConverter;
    }

    private JSONManager()
    {
        //Private constructor
    }
}
