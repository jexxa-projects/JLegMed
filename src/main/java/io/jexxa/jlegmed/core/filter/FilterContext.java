package io.jexxa.jlegmed.core.filter;

import java.util.HashMap;
import java.util.Optional;

public class FilterContext {
    private final HashMap<String, Object> dataState = new HashMap<>();
    private final FilterState filterState = new FilterState();
    private FilterProperties filterProperties;
    private Object filterConfig;

    public void setFilterProperties(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }


    public void setFilterConfig(Object filterConfig) {
        this.filterConfig = filterConfig;
    }

    public <T> Optional<T> getConfig(Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(filterConfig));
    }

    public Optional<FilterProperties> filterProperties()
    {
        return Optional.ofNullable(filterProperties);
    }

    public FilterState getState() {
        return filterState;
    }

    public <T> Optional<T> getState(String id, Class<T> clazz)
    {
        return Optional.ofNullable(clazz.cast(dataState.get(id)));
    }

    public <T> T updateState(String id, T data) {
        dataState.put(id, data);
        return data;
    }
    public static String stateID(Class<?> type, String id)
    {
        return type.getSimpleName() + id;
    }

}
