package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.Content;

public class ProcessingException extends RuntimeException {
    public ProcessingException(AbstractFlowGraph flowGraph, Content data, Throwable throwable)
    {
        super(getMessage(flowGraph, data, throwable), throwable);
    }

    private static String getMessage(AbstractFlowGraph flowGraph, Content data, Throwable throwable)
    {
        return throwable.getClass() + " occurred in FlowGraph " + flowGraph.getFlowGraphID()
                + ". Could not process data " + data.toString();
    }
}
