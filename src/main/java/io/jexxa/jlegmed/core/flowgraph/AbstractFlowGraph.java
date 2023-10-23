package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.processor.TypedInputPipe;
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


    public <U, V> TypedConnector<V> andProcessWith(BiFunction<U, Context, V> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        if ( currentProcessor != null) {
            TypedOutputPipe<U> outputPipe = (TypedOutputPipe<U>) currentProcessor.getOutputPipe();
            outputPipe.connectTo(successor.getInputPipe());
        } else if (sourceOutputPipe != null){
            TypedOutputPipe<U> outputPipe =  (TypedOutputPipe<U>)sourceOutputPipe;
            outputPipe.connectTo(successor.getInputPipe());
        }

        this.currentProcessor = successor;
        processorList.add(currentProcessor);
        return new TypedConnector<>(successor.getOutputPipe(), null);
    }

    public <U, V> TypedConnector<V> andProcessWith(Function<U,V> successorFunction)
    {
        var successor = new TypedProcessor<>(successorFunction);
        if ( currentProcessor != null) {
            TypedOutputPipe<U> outputPipe = (TypedOutputPipe<U>) currentProcessor.getOutputPipe();
            outputPipe.connectTo(successor.getInputPipe());
        }  else if (sourceOutputPipe != null) {
            TypedOutputPipe<U> outputPipe =  (TypedOutputPipe<U>)sourceOutputPipe;
            outputPipe.connectTo(successor.getInputPipe());        }

        this.currentProcessor = successor;
        processorList.add(currentProcessor);
        return new TypedConnector<>(successor.getOutputPipe(), null);
    }



    @Override
    public void processMessage(Content content)
    {
        try {
            if (!processorList.isEmpty())
            {
                TypedInputPipe<T> inputPipe = (TypedInputPipe<T>) processorList.get(0).getInputPipe();
                inputPipe.receive(content.getData(getInputData()), context);
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
