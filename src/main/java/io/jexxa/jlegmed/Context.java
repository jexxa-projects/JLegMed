package io.jexxa.jlegmed;

import java.util.HashMap;
import java.util.Properties;

public class Context {
    private final HashMap<String, Object> contextData = new HashMap<>();
    private final Properties properties;

    public Context(Properties properties)
    {
        this.properties = properties;
    }

    public <T> T getContextData(String id, Class<T> clazz, T startValue)
    {
        contextData.putIfAbsent(id, startValue);
        return clazz.cast(contextData.get(id));
    }

    public Properties getProperties()
    {
        return properties;
    }

    public void updateContextData(String id, Object data) {
        contextData.put(id, data);
    }

    public static String idOf(Class<?> type, String id)
    {
        return type.getSimpleName() + id;
    }
}
