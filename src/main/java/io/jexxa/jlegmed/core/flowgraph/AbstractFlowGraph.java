package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.processor.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractFlowGraph<T> implements FlowGraph {

    private final List<Processor> processorList = new ArrayList<>();
    private Processor currentProcessor ;

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
        return flowGraphID;
    }

    public Context getContext()
    {
        return context;
    }

    private static class TypedProcessor<U, V> implements Processor {
        private BiFunction<U, Context,V> contextFunction;
        private Function<U, V> processFunction;

        private Object processorConfiguration;
        public TypedProcessor(BiFunction<U, Context,V> contextFunction)
        {
            this.contextFunction = contextFunction;
        }

        public TypedProcessor(Function<U, V> processFunction)
        {
            this.processFunction = processFunction;
        }


        @Override
        public Content process(Content content, Context context) {
            if (processFunction != null)
            {
                var result = processFunction.apply((U) content.getData());
                if (result != null)
                {
                    return new Content(result);
                }
                return null;
            }

            if (contextFunction != null)
            {
                var result = contextFunction.apply((U) content.getData(), context);
                if (result != null)
                {
                    return new Content(result);
                }
                return null;
            }

            return null;
        }

        @Override
        public <T> void setConfiguration(T configuration) {
            this.processorConfiguration = configuration;
        }

        @Override
        public Object getConfiguration() {
            return this.processorConfiguration;
        }

    }
}
