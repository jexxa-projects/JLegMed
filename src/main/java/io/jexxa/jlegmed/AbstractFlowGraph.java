package io.jexxa.jlegmed;

import io.jexxa.jlegmed.processor.ContextProcessor;
import io.jexxa.jlegmed.processor.Processor;

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


    public <T extends Processor> void andProcessWith(Class<T> clazz)
    {
        try {
            this.processorList.add(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }

    }

    @Override
    public void andProcessWith(Processor processor) {
        processorList.add(processor);
    }

    @Override
    public void andProcessWith(ContextProcessor processor) {
        processorList.add(new MyPropertiesProcessor(processor, context));
    }

    @Override
    public void processMessage(Message message)
    {
        var result = message;

        for (Processor processor : processorList) {
            result = processor.process(result);
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


    private static class MyPropertiesProcessor implements Processor
    {

        private final Context context;
        private final ContextProcessor contextProcessor;

        public MyPropertiesProcessor(ContextProcessor processor, Context context)
        {
            this.context = context;
            this.contextProcessor = processor;
        }

        @Override
        public Message process(Message message) {
            return contextProcessor.process(message, context);
        }
    }

    protected Context getContext()
    {
        return context;
    }
}
