package io.jexxa.jlegmed.core.flowgraph.builder;

import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.jlegmed.core.filter.processor.Processor;

public class Step<T, R> {
    private final Processor<T, R> processor;
    private final String propertiesName;

    Step(Processor<T, R> processor, String propertiesName) {
        this.processor = processor;
        this.propertiesName = propertiesName;
    }

    Step(Processor<T, R> processor) {
        this.processor = processor;
        this.propertiesName = "";
    }

    public Processor<T, R> processor() {
        return processor;
    }

    public static <T, R> Step<T, R> step(SerializableFunction<T, R> serializableFunction) {
        return new Step<>( Processor.processor(serializableFunction)
                .noPropertiesRequired());
    }

    public static <T, R> Step<T, R> configuredStep(SerializableFunction<T, R> serializableFunction) {
        var processor = Processor.processor(serializableFunction);
        return new Step<>( processor, processor.defaultPropertiesName());
    }

}