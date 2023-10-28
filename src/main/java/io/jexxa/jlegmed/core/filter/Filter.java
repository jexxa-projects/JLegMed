package io.jexxa.jlegmed.core.filter;

import java.util.Optional;

/**
 * A filter is an object that can be used to produce or process the data
 */
public abstract class Filter {
    private final FilterState filterState = new FilterState();
    private FilterProperties filterProperties;
    private Object filterConfig;

    public void init() {}
    public void start() {}
    public void stop() {}
    public void deInit() {}

    public FilterState getState() {
        return filterState;
    }


    protected <R> Optional<R> getConfig(Class<R> configType)
    {
        try {
            return Optional.ofNullable(configType.cast(filterConfig));
        } catch (ClassCastException e)
        {
            return Optional.empty();
        }
    }


    public <U> void setConfig(U configuration) {
        this.filterConfig = configuration;
    }
    public void setProperties(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    public Optional<FilterProperties> getProperties() {
        return Optional.ofNullable(filterProperties);
    }

    public Optional<Object> getConfig() {
        return Optional.ofNullable(filterConfig);
    }


}
