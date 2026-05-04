package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.jlegmed.core.filter.processor.Processor;

import static io.jexxa.jlegmed.core.filter.processor.Processor.consumer;

public class SinkStep<T> {
    private final Processor<T, T, ?> processor;
    private SinkStep(Processor<T, T, ?> processor) {
        this.processor = processor;
    }

    public Processor<T, T, ?> processor() {return processor;}

    public static <T> SinkStep<T> sinkStep(SerializableConsumer<T> serializableConsumer) {
        return new SinkStep<>(consumer(serializableConsumer)
                .noPropertiesRequired());
    }
}
