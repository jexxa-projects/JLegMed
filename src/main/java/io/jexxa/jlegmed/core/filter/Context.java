package io.jexxa.jlegmed.core.filter;

import java.util.HashMap;
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
    private FilterContext filterContext;

    public Context(Properties properties)
    {
        this.properties = properties;
    }

    /**
     * Returns the properties of current filter
     */
    public Properties getProperties() {
       return properties;
    }

    public FilterContext getFilterContext()
    {
        return filterContext;
    }

    public void setFilterContext(FilterContext filterContext) {
        this.filterContext = filterContext;
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

    public record FilterContext (FilterState filterState, Optional<FilterProperties> filterProperties, Optional<Object> filterConfig){
        public <T> T getConfig(Class<T> clazz)
        {
            return clazz.cast(filterConfig.orElse(null));
        }
    }
}
