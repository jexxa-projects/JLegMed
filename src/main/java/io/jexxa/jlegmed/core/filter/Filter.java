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

    public void reachStarted()
    {
        init();
        start();
    }

    public void reachDeInit()
    {
        stop();
        deInit();
    }

    public <U> void config(U configuration) {
        this.filterContext.filterConfig(configuration);
    }
    public void properties(FilterProperties filterProperties) {
        this.filterContext.filterProperties(filterProperties);
    }

    public ProcessingState processingState() {
        return filterContext.processingState();
    }

    public Optional<FilterProperties> properties() {
        return filterContext.filterProperties();
    }

    protected FilterContext filterContext() {
        return filterContext;
    }

    protected void startProcessing() {
        this.filterContext.processingState().start();
    }

    protected void finishedProcessing() {
        this.filterContext.processingState().finished();
    }

    protected boolean processAgain() {
        return this.filterContext.processingState().isProcessingAgain();
    }

}
