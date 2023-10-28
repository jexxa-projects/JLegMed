package io.jexxa.jlegmed.core.filter;

import java.util.Optional;

public abstract class Filter {
    private final FilterConfig filterConfig = new FilterConfig();
    public void init() {}
    public void start() {}
    public void stop() {}
    public void deInit() {}

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }


    protected <R> Optional<R> getFilterConfig(Class<R> configType)
    {
        try {
            return Optional.ofNullable(getFilterConfig().getConfig(configType));
        } catch (ClassCastException e)
        {
            return Optional.empty();
        }
    }


    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
    }
    public void setProperties(PropertiesConfig propertiesConfig) {
        this.filterConfig.setProperties(propertiesConfig);
    }

}
