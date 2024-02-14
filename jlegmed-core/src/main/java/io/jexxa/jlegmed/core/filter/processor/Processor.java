package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.SerializableBiConsumer;
import io.jexxa.jlegmed.core.filter.SerializableBiFunction;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

public abstract class Processor<T, R>  extends Filter {
    private final InputPipe<T> inputPipe = new InputPipe<>(this);
    private final OutputPipe<R> outputPipe = new OutputPipe<>();
    private final boolean filterContextRequired;

    protected Processor(boolean filterContextRequired) {
        this.filterContextRequired = filterContextRequired;
    }

    @Override
    public void init()
    {
        super.init();
        if (filterContextRequired && (filterProperties().name().isEmpty())) {
                SLF4jLogger.getLogger(Processor.class).warn("Lambda expression in Processor requires FilterContext, but no FilterProperties defined using `useProperties`");
        }
    }
    public InputPipe<T> inputPipe()
    {
        return inputPipe;
    }

    public OutputPipe<R> outputPipe()
    {
        return outputPipe;
    }

    public void process(T data) {

        do {
            startProcessing();

            outputPipe().forward(doProcess(data));

            finishedProcessing();

        } while (processAgain());
    }

    protected abstract R doProcess(T data);

    public static  <T, R> Processor<T, R> processor(SerializableBiFunction<T, FilterContext, R> processFunction)
    {
        return new Processor<>(true) {
            @Override
            protected R doProcess(T data) {
                return processFunction.apply(data, filterContext());
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(PipedProcessor<T, R> pipedProcessor)
    {
        return new Processor<>(true) {
            @Override
            protected R doProcess(T data) {
                pipedProcessor.processData(data, filterContext(), outputPipe());
                return null;
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(SerializableFunction<T, R> processFunction)
    {
        return new Processor<>(false) {
            @Override
            protected R doProcess(T data) {
                return processFunction.apply(data);
            }
        };
    }

    public static  <T> Processor<T, T> consumer(SerializableBiConsumer<T, FilterContext> processFunction)
    {
        return new Processor<>(true) {
            @Override
            protected T doProcess(T data) {
                processFunction.accept(data, filterContext());
                return null;
            }
        };
    }

    public static  <T> Processor<T, T> consumer(SerializableConsumer<T> processFunction)
    {
        return new Processor<>(false) {
            @Override
            protected T doProcess(T data) {
                processFunction.accept(data);
                return null;
            }
        };
    }
}
