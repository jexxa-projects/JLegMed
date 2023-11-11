package io.jexxa.jlegmed.plugins.monitor;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;

import java.util.ArrayList;
import java.util.List;

public class LogMonitor {
    private int iterationCounter = 0;
    private final List<Object> iterationData = new ArrayList<>();
    private Object producerOutputPipe;

    private final Runnable iterationLogger;

    public enum LogStyle{BINDING_STYLE, FILTER_STYLE}

    public LogMonitor(LogStyle logStyle)
    {
        if (logStyle == LogStyle.BINDING_STYLE)
        {
            iterationLogger = this::logIterationBindingStyle;
        } else {
            iterationLogger = this::logIterationFilterStyle;
        }
    }
    public void intercept(InvocationContext invocationContext)
    {
        initProducerOutputPipe(invocationContext);
        logIteration(invocationContext);
        iterationData.add(invocationContext.getArgs()[0]);
    }

    private void initProducerOutputPipe(InvocationContext invocationContext)
    {
        if (producerOutputPipe == null)
        {
            this.producerOutputPipe = invocationContext.getTarget();
        }
    }

    private void logIteration(InvocationContext invocationContext)
    {
        if (producerOutputPipe == invocationContext.getTarget() && !iterationData.isEmpty())
        {
            iterationLogger.run();
            ++iterationCounter;
            iterationData.clear();
        }
    }


    private void logIterationBindingStyle()
    {
        // Build a list of messages for each iteration
        iterationData.remove(iterationData.size() - 1); // data from the last output pipe are not required
        StringBuilder sb = new StringBuilder();
        sb.append("Iteration ")
                .append(iterationCounter)
                .append(" : ");

        iterationData.forEach( data -> sb.append(data).append( " -> " ));
        sb.append( "finish " );

        var iterationDataString = sb.toString();

        SLF4jLogger.getLogger(LogMonitor.class.getSimpleName()).info(iterationDataString);
    }

    private void logIterationFilterStyle()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Iteration ")
                .append(iterationCounter)
                .append(" : ")
                .append( " () -> " )
                .append(iterationData.get(0));

        for (int i = 0; i < iterationData.size() - 2 ; i++)
        {
            sb.append(" | ")
                    .append(iterationData.get(i))
                    .append( " -> " )
                    .append(iterationData.get(i+1));
        }

        var iterationDataString = sb.toString();

        SLF4jLogger.getLogger(LogMonitor.class.getSimpleName()).info(iterationDataString);
    }

    public static LogMonitor logBindings()
    {
        return new LogMonitor(LogStyle.BINDING_STYLE);
    }

    public static LogMonitor logFilter()
    {
        return new LogMonitor(LogStyle.FILTER_STYLE);
    }
}
