package io.jexxa.jlegmed.core.flowgraph;

public class ProcessingException extends RuntimeException {
    public ProcessingException(AbstractFlowGraph flowGraph, Content data, Throwable throwable)
    {
        super(getMessage(flowGraph, data, throwable), throwable);
    }

    private static String getMessage(AbstractFlowGraph flowGraph, Content data, Throwable throwable)
    {
        if (throwable instanceof ClassCastException ) {
            return throwable.getClass().getSimpleName() + " occurred in FlowGraph " + flowGraph.getFlowGraphID()
                    + ". Could not process data " + data.toString();
        }

        return throwable.getClass().getSimpleName() + " occurred in FlowGraph " + flowGraph.getFlowGraphID()
                + ". Could not process data " + data.toString() + " -> Reason: " + throwable.getMessage();

    }
}
