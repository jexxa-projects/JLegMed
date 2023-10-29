package io.jexxa.jlegmed.core.filter;

import java.util.Optional;

/**
 * A filter is an object that can be used to produce or process the data
 */
public abstract class Filter {
    private final FilterContext filterContext = new FilterContext();


    public void init() {}
    public void start() {}
    public void stop() {}
    public void deInit() {}

    public FilterState getState() {
        return filterContext.getState();
    }


    protected <R> Optional<R> getConfig(Class<R> configType)
    {
        try {
            return filterContext.getConfig(configType);
        } catch (ClassCastException e)
        {
            return Optional.empty();
        }
    }


    public <U> void setConfig(U configuration) {
        this.filterContext.setFilterConfig(configuration);
    }
    public void setProperties(FilterProperties filterProperties) {
        this.filterContext.setFilterProperties(filterProperties);
    }

    public Optional<FilterProperties> getProperties() {
        return filterContext.filterProperties();
    }

    protected FilterContext getFilterContext() {
        return filterContext;
    }
}
