package io.jexxa.jlegmed.core.filter;

public abstract class Filter {
    private final FilterConfig filterConfig = new FilterConfig();
    public void init() {}
    public void start() {}
    public void stop() {}
    public void deInit() {}

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public <U> void setConfiguration(U configuration) {
        this.filterConfig.setConfig(configuration);
    }
    public void setProperties(PropertiesConfig propertiesConfig) {
        this.filterConfig.setProperties(propertiesConfig);
    }

}
