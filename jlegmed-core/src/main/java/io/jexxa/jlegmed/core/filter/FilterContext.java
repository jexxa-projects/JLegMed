package io.jexxa.jlegmed.core.filter;

import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class FilterContext {
    private final HashMap<String, Object> dataState = new HashMap<>();
    private final ProcessingState processingState = new ProcessingState();
    private FilterProperties filterProperties = new FilterProperties("", new Properties());

    public void filterProperties(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    public FilterProperties filterProperties()
    {
        return filterProperties;
    }
    public Properties properties() { return filterProperties.properties(); }
    public String propertiesName() { return filterProperties.propertiesName(); }

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
