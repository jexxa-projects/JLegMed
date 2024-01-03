package io.jexxa.jlegmed.core.filter;

import java.util.Properties;

/**
 * A filter is an object that can be used to produce or process the data
 */
public abstract class Filter {
    private final FilterContext filterContext = new FilterContext();

    public void init()   { /* Empty default implementation */ }
    public void start()  { /* Empty default implementation */ }
    public void stop()   { /* Empty default implementation */ }
    public void deInit() { /* Empty default implementation */ }

    public Filter reachStarted()
    {
        init();
        start();
        return this;
    }

    public void reachDeInit()
    {
        stop();
        deInit();
    }

    public Filter useProperties(FilterProperties filterProperties) {
        this.filterContext.filterProperties(filterProperties);
        return this;
    }

    protected ProcessingState processingState() {
        return filterContext.processingState();
    }

    @SuppressWarnings("unused")
    public FilterProperties filterProperties() {
        return filterContext.filterProperties();
    }

    /**
     * Returns the properties included in the filterProperties() if available
     *
     * @return Properties included in {@link #filterProperties()} if available
     */
    protected Properties properties() {
        return filterContext
                .filterProperties()
                .properties();
    }

    protected String propertiesName() {
        return filterContext
                .filterProperties()
                .propertiesName();
    }

    protected FilterContext filterContext() {
        return filterContext;
    }

    protected void startProcessing() {
        processingState().start();
    }

    protected void finishedProcessing() {
        processingState().finished();
    }

    protected boolean processAgain() {
        return processingState().isProcessingAgain();
    }

}
