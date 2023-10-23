package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.TypedConnector;
import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.producer.ActiveProducerURL;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericActiveProducerURL<T> implements ActiveProducerURL<T> {
    private GenericActiveProducer<T> genericActiveProducer;
    private TypedConnector<T> typedConnector;
    @Override
    public ActiveProducer<T> init(FlowGraph<T> flowGraph) {
        genericActiveProducer = new GenericActiveProducer<>(flowGraph);
        typedConnector = new TypedConnector<>(genericActiveProducer.getOutputPipe(), null);
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

    public TypedConnector<T> withInterval(int fixedRate, TimeUnit timeUnit)
    {
        genericActiveProducer.setInterval(fixedRate, timeUnit);
        return typedConnector;
    }

    public static <T> GenericActiveProducerURL<T> genericProducerURL() {
        return new GenericActiveProducerURL<>();
    }
}
