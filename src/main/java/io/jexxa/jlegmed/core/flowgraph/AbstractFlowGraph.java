package io.jexxa.jlegmed.core.flowgraph;

import java.util.Properties;

public abstract class AbstractFlowGraph<T> implements FlowGraph {

    private final Context context;

    private final String flowGraphID;

    protected AbstractFlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.context = new Context(properties);
    }

    public  abstract Class<T> getInputData();


    public String getFlowGraphID()
    {
        return flowGraphID;
    }

    public Context getContext()
    {
        return context;
    }


}
