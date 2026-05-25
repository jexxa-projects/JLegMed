package io.jexxa.jlegmed.core.flowgraph.steps;

import io.jexxa.adapterapi.invocation.function.SerializableBiConsumer;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.core.filter.processor.StreamProcessor;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import static io.jexxa.jlegmed.core.filter.processor.Processor.streamProcessor;

public class StreamStep<T, R> extends Step<StreamStep<T, R>>{
    private final StreamProcessor<T, R> processor;

    StreamStep(StreamProcessor<T, R> processor) {
        this.processor = processor;
    }

    public Processor<T, R,?> processor() {
        return processor;
    }

    public static <T, R> StreamStep<T, R> streamStep(SerializableBiConsumer<T, OutputPipe<R>> serializableFunction) {
        return new StreamStep<>( streamProcessor(serializableFunction)
                .noPropertiesRequired());
    }

    public static <T, R> StreamStep<T, R> streamStep(StreamProcessor<T, R> streamProcessor) {
        return new StreamStep<>( streamProcessor
                .noPropertiesRequired());
    }

}