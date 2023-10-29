package io.jexxa.jlegmed.core.filter;

import java.util.HashMap;
import java.util.Optional;

public class FilterContext {
    private final HashMap<String, Object> dataState = new HashMap<>();
    private final ProcessingState processingState = new ProcessingState();
    private FilterProperties filterProperties;
    private Object filterConfig;

    public void filterProperties(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }


    public void filterConfig(Object filterConfig) {
        this.filterConfig = filterConfig;
    }

    public <T> Optional<T> config(Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(filterConfig));
    }

    public Optional<FilterProperties> filterProperties()
    {
        return Optional.ofNullable(filterProperties);
    }

    public ProcessingState processingState() {
        return processingState;
    }

    public <T> Optional<T> state(String id, Class<T> clazz)
    {
        return Optional.ofNullable(clazz.cast(dataState.get(id)));
    }

    public <T> T updateState(String id, T data) {
        dataState.put(id, data);
        return data;
    }

}
