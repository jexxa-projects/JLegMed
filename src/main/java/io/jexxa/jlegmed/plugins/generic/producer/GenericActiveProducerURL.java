package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.core.filter.processor.ProcessorConnector;
import io.jexxa.jlegmed.core.flowgraph.SourceConnector;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducerURL;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericActiveProducerURL<T> implements ActiveProducerURL<T> {
    private GenericActiveProducer<T> genericActiveProducer;
    private ProcessorConnector<T> processorConnector;
    @Override
    public Producer<T> init(SourceConnector<T> sourceConnector) {
        genericActiveProducer = new GenericActiveProducer<>(sourceConnector);
        processorConnector = new ProcessorConnector<>(genericActiveProducer.getOutputPipe(), null);
        return genericActiveProducer;
    }

    public GenericActiveProducerURL<T> using(Function<Context, T> function)
    {
        genericActiveProducer.setFunction(function);
        return this;
    }

    @SuppressWarnings("unused")
    public GenericActiveProducerURL<T> using(Supplier<T> function)
    {
        genericActiveProducer.setSupplier(function);
        return this;
    }

    public ProcessorConnector<T> withInterval(int fixedRate, TimeUnit timeUnit)
    {
        genericActiveProducer.setInterval(fixedRate, timeUnit);
        return processorConnector;
    }

    public static <T> GenericActiveProducerURL<T> genericProducerURL() {
        return new GenericActiveProducerURL<>();
    }
}
