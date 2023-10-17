package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.flowgraph.Content;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.Processor;
import io.jexxa.jlegmed.core.flowgraph.TypedProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractFlowGraph implements FlowGraph {

    private final List<Processor> processorList = new ArrayList<>();
    private Processor currentProcessor ;

    private final Context context;

    private final JLegMed jLegMed;

    protected AbstractFlowGraph(JLegMed jLegMed)
    {
        this.jLegMed = jLegMed;
        this.context = new Context(jLegMed.getProperties());
    }


    public <U, V> AbstractFlowGraph andProcessWith(BiFunction<U, Context, V> processor)
    {
        this.currentProcessor = new TypedProcessor<>(processor);
        processorList.add(currentProcessor);
        return this;
    }

    public <U, V> AbstractFlowGraph andProcessWith(Function<U,V> function)
    {
        this.currentProcessor = new TypedProcessor<>(function);
        processorList.add(currentProcessor);
        return this;
    }


    public  <T> AbstractFlowGraph useConfig(T configuration)
    {
        this.currentProcessor.setConfiguration(configuration);
        return this;
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
