package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.core.filter.producer.PipedProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.flowgraph.FlowGraphScheduler;

import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.core.filter.producer.FunctionalProducer.producer;

public class ProducerBuilder<T> {
    private final Class<T> sourceType;
    private final long fixedRate;
    private final TimeUnit timeUnit;
    private final FlowGraph flowGraph;
    private long maxIteration = -1;

    ProducerBuilder(FlowGraph flowGraph, Class<T> sourceType, long fixedRate, TimeUnit timeUnit) {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    ProducerBuilder(FlowGraph flowGraph, Class<T> sourceType, long fixedRate, TimeUnit timeUnit, long maxIteration) {
        this(flowGraph, sourceType, fixedRate, timeUnit);
        this.maxIteration = maxIteration;
    }


    public Binding<T, T> from(SerializableFunction<FilterContext, T> function) {
        return setProducer(producer(function));
    }

    public Binding<T, T> from(PipedProducer<T> function) {
        return setProducer(producer(function));
    }


    public Binding<T, T> from(SerializableSupplier<T> supplier) {
        return setProducer(producer(supplier));
    }

    public Binding<T, T> from(PassiveProducer<T> producer) {
        producer.producingType(sourceType);
        return setProducer(producer);
    }

    private Binding<T, T> setProducer(PassiveProducer<T> producer)
    {
        if (maxIteration < 0) {
            flowGraph.setProducer(producer, new FlowGraphScheduler.FixedRate(fixedRate, timeUnit));
        } else {
            flowGraph.setProducer(producer, new FlowGraphScheduler.RepeatedRate(maxIteration, new FlowGraphScheduler.FixedRate(fixedRate, timeUnit)));
        }
        return new Binding<>(producer, producer.errorPipe(), producer.outputPipe(), flowGraph);
    }
}
