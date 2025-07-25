package io.jexxa.jlegmed.core.filter;

import java.io.Serializable;
import java.util.Properties;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.classNameFromLambda;
import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

/**
 * A filter is an object that can be used to produce or process the data
 */
@SuppressWarnings("unused")
public abstract class Filter {
    private boolean strictFailFast = false;
    private boolean withoutProperties = false;

    private final FilterContext filterContext = new FilterContext();

    public abstract String name();

    public void init()   { /* Empty default implementation */ }
    public void start()  { /* Empty default implementation */ }
    public void stop()   { /* Empty default implementation */ }
    public void deInit() { /* Empty default implementation */ }

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

    public Filter useProperties(FilterProperties filterProperties) {
        this.withoutProperties = false;
        this.filterContext.filterProperties(filterProperties);
        return this;
    }

    public Filter noPropertiesRequired()
    {
        this.withoutProperties = true;
        return this;
    }

    public boolean isPropertiesRequired()
    {
        return this.withoutProperties;
    }

    protected ProcessingState processingState() {
        return filterContext.processingState();
    }

    @SuppressWarnings("unused")
    public FilterProperties filterProperties() {
        return filterContext.filterProperties();
    }

    /**
     * Returns the properties included in the filterProperties()
     *
     * @return Properties included in {@link #filterProperties()}
     */
    protected Properties properties() {
        return filterContext
                .filterProperties()
                .properties();
    }

    protected String propertiesName() {
        return filterContext
                .filterProperties()
                .name();
    }

    public String defaultPropertiesName()
    {
        return getClass().getSimpleName();
    }

    public void enableStrictFailFast() {
        this.strictFailFast = true;
    }

    public void disableStrictFailFast() {
        this.strictFailFast = false;
    }

    public boolean strictFailFast() {
        return strictFailFast;
    }

    public void strictFailFast(boolean strictFailFast) {
        this.strictFailFast = strictFailFast;
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

    public static String filterNameFromLambda(Serializable lambda)
    {
        return classNameFromLambda(lambda).getSimpleName() + "::" + methodNameFromLambda(lambda);
    }

}
