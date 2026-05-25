package io.jexxa.jlegmed.core.flowgraph.steps;

import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.jlegmed.core.filter.processor.Processor;

public class ProcessorStep<T, R> extends Step<ProcessorStep<T, R>>{
    private final Processor<T, R,?> processor;

    ProcessorStep(Processor<T, R,?> processor) {
        this.processor = processor;
        processor.noPropertiesRequired();
    }

    public Processor<T, R,?> processor() {
        return processor;
    }

    public static <T, R> ProcessorStep<T, R> processorStep(SerializableFunction<T, R> serializableFunction) {
        return new ProcessorStep<>( Processor.processor(serializableFunction)
                .noPropertiesRequired());
    }

    public static <T, R> ProcessorStep<T, R> configuredStep(SerializableFunction<T, R> serializableFunction) {
        var processor = Processor.processor(serializableFunction);
        return new ProcessorStep<>( processor)
                .useProperties(processor.defaultPropertiesName()
                );
    }

}