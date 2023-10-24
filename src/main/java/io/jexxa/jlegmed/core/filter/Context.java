package io.jexxa.jlegmed.core.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Context {
    private final HashMap<String, Object> contextData = new HashMap<>();
    private final Properties properties;
    private FilterConfig filterConfig;

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

    public Properties getProperties(String propertiesPrefix)
    {
        var subset = new Properties();

        List<String> result = properties.keySet().stream()
                .map(Object::toString)
                .filter(string -> string.contains(propertiesPrefix))
                .toList();

        result.forEach( element -> subset.put(
                element.substring(element.lastIndexOf(propertiesPrefix) + propertiesPrefix.length() + 1),
                properties.getProperty(element) ));

        return subset;
    }

    public <T> T getFilterConfig(Class<T> conigType)
    {
        return filterConfig.getConfig(conigType);
    }

    public void setFilterConfig(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    public <T> T update(String id, T data) {
        contextData.put(id, data);
        return data;
    }

    public static String contextID(Class<?> type, String id)
    {
        return type.getSimpleName() + id;
    }

    public boolean isProcessedAgain() {
        return filterConfig.isProcessedAgain();
    }

    public void processAgain()
    {
        filterConfig.processAgain();
    }
}
