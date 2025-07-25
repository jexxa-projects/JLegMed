package io.jexxa.jlegmed.plugins.monitor;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.JLegMed;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlowgraphMonitor {
    private final JLegMed jLegMed;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public FlowgraphMonitor(JLegMed jLegMed)
    {
        this.jLegMed = jLegMed;
    }


    public void start(long interval, TimeUnit timeUnit) {
        Runnable task = () -> {
            try {
                showStats();
            } catch (Exception e) {
                SLF4jLogger.getLogger(FlowgraphMonitor.class).warn(String.valueOf(e));
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, interval, timeUnit);
    }

    public void stop()
    {
        scheduler.shutdown();
    }

    public void showStats()
    {
        jLegMed.getFlowGraphs().forEach( this::showStats );
    }
    private void showStats(String flowGraphName)
    {
        var flowgraph =jLegMed.getFlowGraph(flowGraphName);
        var flowgraphDescription = String.join(" -> ", flowgraph.flowgraphDescription());
        SLF4jLogger.getLogger(FlowgraphMonitor.class).info(" {} : {}", flowGraphName, flowgraphDescription);
        SLF4jLogger.getLogger(FlowgraphMonitor.class).info(" {} : {}", flowGraphName, flowgraph.processingStats());
        SLF4jLogger.getLogger(FlowgraphMonitor.class).info("");
    }

}
