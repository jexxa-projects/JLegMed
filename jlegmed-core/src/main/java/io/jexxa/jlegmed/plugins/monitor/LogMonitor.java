package io.jexxa.jlegmed.plugins.monitor;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.FlowGraphMonitor;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.util.ArrayList;
import java.util.List;

public class LogMonitor extends FlowGraphMonitor {
    private int iterationCounter = 0;

    private final List<Object> iterationData = new ArrayList<>();

    public void intercept(OutputPipe<?> outputPipe, Object data)
    {
        if (producerOutputPipe() == outputPipe && !iterationData.isEmpty())
        {
            logIteration();
            ++iterationCounter;
            iterationData.clear();
        }
        iterationData.add(data);
    }


    private void logIteration()
    {
        // Build a list of messages for each iteration
        iterationData.remove(iterationData.size() - 1); // data from the last output pipe are not required
        StringBuilder sb = new StringBuilder();
        sb.append("Iteration ").append(iterationCounter).append(" : ");
        iterationData.forEach( data -> sb.append(data).append( " -> " ));
        sb.append( "finish " );

        var iterationDataString = sb.toString();

        SLF4jLogger.getLogger(FlowGraphMonitor.class).info(iterationDataString);
    }

    public static LogMonitor logMonitor()
    {
        return new LogMonitor();
    }
}
