package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.Processor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFlowGraph implements FlowGraph {

    private final List<Processor> processorList = new ArrayList<>();

    private final Context context;

    private final JLegMed jLegMed;

    protected AbstractFlowGraph(JLegMed jLegMed)
    {
        this.jLegMed = jLegMed;
        this.context = new Context(jLegMed.getProperties());
    }


    @Override
    public void andProcessWith(Processor processor) {
        processorList.add(processor);
    }

    @Override
    public void processMessage(Content content)
    {
        try {
            var result = content;

            for (var processor : processorList) {
                context.setProcessorConfiguration(processor.getConfiguration());
                result = processor.process(result, context);
                if (result == null) {
                    return;
                }
            }
        } catch (RuntimeException e)
        {
            throw new ProcessingException(this, content, e);
        }
    }

    public String getFlowGraphID()
    {
        return getjLegMed().getFlowgraphID(this);
    }

    protected JLegMed getjLegMed()
    {
        return jLegMed;
    }

    public Context getContext()
    {
        return context;
    }
}
