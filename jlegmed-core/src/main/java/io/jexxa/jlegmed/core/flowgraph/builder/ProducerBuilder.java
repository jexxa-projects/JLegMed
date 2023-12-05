package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.common.drivingadapter.scheduler.RepeatedFixedRate;
import io.jexxa.common.drivingadapter.scheduler.ScheduledFixedRate;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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


    public Binding<T> from(Function<FilterContext, T> function) {
        return configureScheduler(producer(function));
    }

    public Binding<T> from(BiFunction<FilterContext, Class<T>, T> biFunction) {
        return configureScheduler(producer(biFunction, sourceType));
    }

    public Binding<T> from(Supplier<T> supplier) {
        return configureScheduler(producer(supplier, sourceType));
    }

    public Binding<T> from(PassiveProducer<T> producer) {
        producer.producingType(sourceType);
        return configureScheduler(producer);
    }

    private Binding<T> configureScheduler(PassiveProducer<T> producer)
    {
        Scheduler scheduler = new Scheduler();
        scheduler.register(createListener(producer));

        flowGraph.setProducer(producer, scheduler);

        return new Binding<>(producer, producer.outputPipe(), flowGraph);
    }

    private Object createListener(PassiveProducer<T> producer)
    {
        if (maxIteration < 0 ){
            return new ScheduledFixedRate(producer::produceData, 0, fixedRate, timeUnit);
        } else {
            return new RepeatedFixedRate(maxIteration, producer::produceData, 0, fixedRate, timeUnit);
        }
    }
}
