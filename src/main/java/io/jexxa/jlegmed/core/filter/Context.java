package io.jexxa.jlegmed.core.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
/**
 *  A context provides the following information:
 *  - State information used by internal filters
 *  - Properties of the main application
 *  - FilterConfig of the current executed filter
 */
public class Context {
    private final HashMap<String, Object> contextState = new HashMap<>();
    private final Properties properties;
    private FilterConfig filterConfig;

    public Context(Properties properties)
    {
        this.properties = properties;
    }

    public Optional<PropertiesConfig> getPropertiesConfig()
    {
        if (filterConfig != null)
        {
            return Optional.ofNullable(filterConfig.getPropertiesConfig());
        }

        return Optional.empty();
    }

    /**
     * Returns the properties of current filter
     */
    public Optional<Properties> getProperties() {
        if (filterConfig != null && filterConfig.getPropertiesConfig() != null)
        {
            return getProperties(filterConfig.getPropertiesConfig().properties());
        }

        return Optional.empty();
    }

    public Optional<Properties> getProperties(String propertiesPrefix)
    {
        List<String> result = properties.keySet().stream()
                .map(Object::toString)
                .filter(string -> string.contains(propertiesPrefix))
                .toList();

        if (result.isEmpty())
        {
            return Optional.empty();
        }

        var subset = new Properties();
        result.forEach( element -> subset.put(
                element.substring(element.lastIndexOf(propertiesPrefix) + propertiesPrefix.length() + 1),
                properties.getProperty(element) ));

        return Optional.of(subset);
    }

    public <T> T getFilterConfig(Class<T> configType)
    {
        return filterConfig.getConfig(configType);
    }

    public void setFilterConfig(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }


    public boolean isProcessingFinished() {
        return !filterConfig.isProcessedAgain();
    }

    public void processAgain()
    {
        filterConfig.processAgain();
    }


    public <T> Optional<T> getState(String id, Class<T> clazz)
    {
        return Optional.ofNullable(clazz.cast(contextState.get(id)));
    }

    public <T> T updateState(String id, T data) {
        contextState.put(id, data);
        return data;
    }
    public static String stateID(Class<?> type, String id)
    {
        return type.getSimpleName() + id;
    }
}
