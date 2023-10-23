package io.jexxa.jlegmed.core.flowgraph;

import java.util.Properties;

public abstract class FlowGraph<T> {
    private final Context context;

    private final String flowGraphID;

    protected FlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.context = new Context(properties);
    }

    public String getFlowGraphID()
    {
        return flowGraphID;
    }

    public Context getContext()
    {
        return context;
    }

    public  abstract Class<T> getInputData();
    public abstract void start();
    public abstract void stop();

}
