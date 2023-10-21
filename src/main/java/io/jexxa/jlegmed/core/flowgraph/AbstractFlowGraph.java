package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.processor.Processor;
import io.jexxa.jlegmed.core.processor.TypedProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractFlowGraph<T> implements FlowGraph {

    private final List<Processor> processorList = new ArrayList<>();
    private TypedProcessor<?,?> currentProcessor ;

    private final Context context;

    private final String flowGraphID;

    protected AbstractFlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.context = new Context(properties);
    }

    public  abstract Class<T> getInputData();


    public <U, V> AbstractFlowGraph<T> andProcessWith(BiFunction<U, Context, V> processor)
    {
        this.currentProcessor = new TypedProcessor<>(processor);
        processorList.add(currentProcessor);
        return this;
    }

    public <U, V> AbstractFlowGraph<T> andProcessWith(Function<U,V> function)
    {
        this.currentProcessor = new TypedProcessor<>(function);
        processorList.add(currentProcessor);
        return this;
    }


    public  <U> AbstractFlowGraph<T> useConfig(U configuration)
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
        return flowGraphID;
    }

    public Context getContext()
    {
        return context;
    }

}
