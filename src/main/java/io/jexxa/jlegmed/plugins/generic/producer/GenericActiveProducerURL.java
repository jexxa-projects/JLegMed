package io.jexxa.jlegmed.plugins.generic.producer;

import io.jexxa.jlegmed.core.flowgraph.AbstractFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.Context;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.producer.ActiveProducer;
import io.jexxa.jlegmed.core.producer.ActiveProducerURL;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericActiveProducerURL implements ActiveProducerURL {
    private FlowGraph flowGraph;
    private GenericActiveProducer genericActiveProducer;

    @Override
    public <U> ActiveProducer init(AbstractFlowGraph<U> flowGraph) {
        this.flowGraph = flowGraph;
        genericActiveProducer = new GenericActiveProducer(flowGraph);
        return genericActiveProducer;
    }

    public GenericActiveProducerURL using(Function<Context, Object> function)
    {
        genericActiveProducer.setFunction(function);
        return this;
    }

    public GenericActiveProducerURL using(Supplier<Object> function)
    {
        genericActiveProducer.setSupplier(function);
        return this;
    }

    public FlowGraph withInterval(int fixedRate, TimeUnit timeUnit)
    {
        genericActiveProducer.setInterval(fixedRate, timeUnit);
        return flowGraph;
    }

    public static GenericActiveProducerURL genericProducerURL() {
        return new GenericActiveProducerURL();
    }
}
