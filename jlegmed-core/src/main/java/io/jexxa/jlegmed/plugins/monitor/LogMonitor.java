package io.jexxa.jlegmed.plugins.monitor;

import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogMonitor {
    private int filterCounter = 0;
    private int iterationCounter = 0;
    private final Map<Object, FilterDescription> filterDescription = new LinkedHashMap<>();

    private final List<IterationEntry> iterationData = new ArrayList<>();

    private Object producerOutputPipe;

    private final Runnable iterationLogger;

    public enum LogStyle{DATAFLOW_STYLE, FUNCTION_STYLE}

    public LogMonitor(LogStyle logStyle)
    {
        if (logStyle == LogStyle.DATAFLOW_STYLE)
        {
            iterationLogger = this::logIterationBindingStyle;
        } else {
            iterationLogger = this::logIterationFilterStyle;
        }
    }
    public void intercept(InvocationContext invocationContext)
    {
        initFilter(invocationContext);
        addIterationData(invocationContext);
        logIteration(invocationContext);
    }

    private void initFilter(InvocationContext invocationContext)
    {
        if (producerOutputPipe == null)
        {
            this.producerOutputPipe = invocationContext.getTarget();
        }
        filterDescription.computeIfAbsent(invocationContext.getTarget(), this::createFilterDescription);
    }

    private void logIteration(InvocationContext invocationContext)
    {
        if (invocationContext.getArgs()[0] == null)
        {
            iterationLogger.run();
            iterationData.clear();
        }

        if (invocationContext.getTarget() == producerOutputPipe) {
            ++iterationCounter;
        }

    }

    private void addIterationData(InvocationContext context)
    {
        iterationData.add(new IterationEntry(context.getTarget(), context.getArgs()[0]));
    }

    private FilterDescription createFilterDescription(Object outputPipe)
    {
        var description = new FilterDescription(filterCounter, ((OutputPipe<?>)outputPipe).filter().name());
        ++filterCounter;
        return description;
    }

    private void logIterationBindingStyle()
    {
        // Build a list of messages for each iteration
        filterDescription.remove(filterDescription.size() - 1); // data from the last output pipe are not required
        StringBuilder sb = new StringBuilder();
        sb.append("Iteration ")
                .append(iterationCounter)
                .append(" : ");

        iterationData.forEach( entry -> sb.append("[Binding ").append(getIndex(entry.outputPipe())).append("] ").append(entry.producedData()).append( " -> " ));
        sb.append( "finish " );

        var iterationDataString = sb.toString();

        SLF4jLogger.getLogger(LogMonitor.class).info(iterationDataString);
    }

    private void logIterationFilterStyle()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Iteration ")
                .append(iterationCounter)
                .append(" (FilterStyle) : ")
                .append( " [" ).append(getFilterName(iterationData.get(0).outputPipe())).append( "] ")
                .append( " () -> " )
                .append(iterationData.get(0).producedData());

        for (int i = 0; i < iterationData.size() - 1 ; i++)
        {
            sb.append(" | ")
                    .append( " [" ).append(getFilterName(iterationData.get(i+1).outputPipe())).append( "] ")
                    .append(iterationData.get(i).producedData())
                    .append( " -> " )
                    .append(iterationData.get(i+1).producedData());
        }

        var iterationDataString = sb.toString();

        SLF4jLogger.getLogger(LogMonitor.class).info(iterationDataString);
    }

    int getIndex(Object outputPipe)
    {
        if (filterDescription.containsKey(outputPipe)) {
            return filterDescription.get(outputPipe).index();
        } else {
            return -1;
        }
    }

    String getFilterName(Object outputPipe)
    {
        if (filterDescription.containsKey(outputPipe)) {
            return filterDescription.get(outputPipe).filterName();
        } else {
            return "UNKNOWN FILTER";
        }
    }




    public static BeforeInterceptor logDataFlowStyle()
    {
        return new LogMonitor(LogStyle.DATAFLOW_STYLE)::intercept;
    }

    public static BeforeInterceptor logFunctionStyle()
    {
        return new LogMonitor(LogStyle.FUNCTION_STYLE)::intercept;
    }

    private record FilterDescription(int index, String filterName) { }

    record IterationEntry(Object outputPipe, Object producedData){}
}
