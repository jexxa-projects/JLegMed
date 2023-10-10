package io.jexxa.jlegmed.core;

import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class Context {
    private final HashMap<String, Object> contextData = new HashMap<>();
    private final Properties properties;

    public Context(Properties properties)
    {
        this.properties = properties;
    }

    public <T> Optional<T> get(String id, Class<T> clazz)
    {
        return Optional.ofNullable(clazz.cast(contextData.get(id)));
    }

    @SuppressWarnings("unused")
    public Properties getProperties()
    {
        return properties;
    }


    public <T> T update(String id, T data) {
        contextData.put(id, data);
        return data;
    }

    public static String contextID(Class<?> type, String id)
    {
        return type.getSimpleName() + id;
    }
}
