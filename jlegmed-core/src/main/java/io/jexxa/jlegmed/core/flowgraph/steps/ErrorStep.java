package io.jexxa.jlegmed.core.flowgraph.steps;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.processor.Processor;

import static io.jexxa.jlegmed.core.filter.processor.Processor.consumer;

public class ErrorStep<T> extends Step<ErrorStep<T>>{
    private final Processor<ProcessingError<T>, ProcessingError<T>,?> processor;

    ErrorStep(SerializableConsumer<ProcessingError<T>> errorHandler) {
        this.processor = consumer(errorHandler);
        processor.noPropertiesRequired();
    }

    ErrorStep(Processor<ProcessingError<T>, ProcessingError<T>,?> errorHandler) {
        this.processor = errorHandler;
    }

    public Processor<ProcessingError<T>, ProcessingError<T>,?> processor() {
        return processor;
    }

    public static <T> ErrorStep<T> errorStep(SerializableConsumer<ProcessingError<T>> serializableFunction) {
        return new ErrorStep<>( consumer(serializableFunction)
                .noPropertiesRequired());
    }

}