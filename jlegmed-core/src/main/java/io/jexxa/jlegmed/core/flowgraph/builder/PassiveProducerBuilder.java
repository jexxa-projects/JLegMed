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

public class PassiveProducerBuilder<T> {
    private final Class<T> sourceType;
    private final long fixedRate;
    private final TimeUnit timeUnit;
    private final FlowGraph flowGraph;
    private long maxIteration = -1;

    PassiveProducerBuilder(FlowGraph flowGraph, Class<T> sourceType, long fixedRate, TimeUnit timeUnit) {
        this.flowGraph = flowGraph;
        this.sourceType = sourceType;
        this.fixedRate = fixedRate;
        this.timeUnit = timeUnit;
    }

    PassiveProducerBuilder(FlowGraph flowGraph, Class<T> sourceType, long fixedRate, TimeUnit timeUnit, long maxIteration) {
        this(flowGraph, sourceType, fixedRate, timeUnit);
        this.maxIteration = maxIteration;
    }


    public Binding<T> from(Function<FilterContext, T> function) {
        return configure(producer(function));
    }

    public Binding<T> from(BiFunction<FilterContext, Class<T>, T> biFunction) {
        return configure(producer(biFunction));
    }

    public Binding<T> from(Supplier<T> supplier) {
        return configure(producer(supplier));
    }

    public Binding<T> from(PassiveProducer<T> producer) {
        return configure(producer);
    }

    private Binding<T> configure(PassiveProducer<T> producer)
    {
        Scheduler scheduler = new Scheduler();

        scheduler.register(createListener(producer));
        producer.drivingAdapter(scheduler);
        producer.producingType(sourceType);
        flowGraph.setProducer(producer);

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
