package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.processor.TypedOutputPipe;
import io.jexxa.jlegmed.core.processor.TypedProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractFlowGraph<T> implements FlowGraph {

    private final List<TypedProcessor<?,?>> processorList = new ArrayList<>();
    private TypedProcessor<?,?> currentProcessor ;
    private TypedOutputPipe<?> sourceOutputPipe;

    private final Context context;

    private final String flowGraphID;

    protected AbstractFlowGraph(String flowGraphID, Properties properties)
    {
        this.flowGraphID = flowGraphID;
        this.context = new Context(properties);
    }

    public  abstract Class<T> getInputData();


    public <U, V> AbstractFlowGraph<T> andProcessWith(BiFunction<U, Context, V> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        if ( currentProcessor != null) {
            currentProcessor.getOutputPipe().connectTo(successor.getInputPipe());
        } else if (sourceOutputPipe != null){
            sourceOutputPipe.connectTo(successor.getInputPipe());
        }

        this.currentProcessor = successor;
        processorList.add(currentProcessor);
        return this;
    }

    public <U, V> AbstractFlowGraph<T> andProcessWith(Function<U,V> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        if ( currentProcessor != null) {
            currentProcessor.getOutputPipe().connectTo(successor.getInputPipe());
        }  else if (sourceOutputPipe != null) {
            sourceOutputPipe.connectTo(successor.getInputPipe());
        }

        this.currentProcessor = successor;
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
            if (!processorList.isEmpty())
            {
                processorList.get(0).getInputPipe().receive(content, context);
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

    protected void setProducerOutputPipe(TypedOutputPipe<?> outputPipe)
    {
        this.sourceOutputPipe = outputPipe;
    }

}
