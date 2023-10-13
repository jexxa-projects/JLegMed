package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class AbstractFlowGraph implements FlowGraph {

    private final List<Processor> processorList = new ArrayList<>();

    private final Context context = new Context(new Properties());

    private final JLegMed jLegMed;

    protected AbstractFlowGraph(JLegMed jLegMed)
    {
        this.jLegMed = jLegMed;
    }


    @Override
    public void andProcessWith(Processor processor) {
        processorList.add(processor);
    }

    @Override
    public void processMessage(Content content)
    {
        var result = content;

        for (var processor : processorList) {
            result = processor.process(result, context);
            if (result == null)
            {
                return;
            }
        }
    }

    protected JLegMed getjLegMed()
    {
        return jLegMed;
    }

    protected Context getContext()
    {
        return context;
    }
}
