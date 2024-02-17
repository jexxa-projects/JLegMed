package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableBiConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

public abstract class Processor<T, R>  extends Filter {
    private final InputPipe<T> inputPipe = new InputPipe<>(this);
    private final OutputPipe<R> outputPipe = new OutputPipe<>();
    private final OutputPipe<T> errorPipe = new OutputPipe<>();
    private final boolean filterContextRequired;
    private final String name;

    protected Processor(boolean filterContextRequired, String name) {
        this.filterContextRequired = filterContextRequired;
        this.name = name;
    }

    @Override
    public void init()
    {
        super.init();
        if (strictFailFastWarning()) {
                SLF4jLogger.getLogger(Processor.class).warn("`{}` requires FilterContext -> Either define properties with `useProperties` or call `withoutProperties` ", name());
        }
        if (strictFailFast() && strictFailFastWarning())
        {
            throw new FailFastException("Strict fail fast is enabled: `"+  name() + "` requires FilterContext -> Either define properties with `useProperties` or call `withoutProperties` ");
        }
    }

    @Override
    public String name() {
        return name;
    }

    public InputPipe<T> inputPipe()
    {
        return inputPipe;
    }

    public OutputPipe<R> outputPipe()
    {
        return outputPipe;
    }
    public OutputPipe<T> errorPipe()
    {
        return errorPipe;
    }

    public void process(T data) {

        do {
                startProcessing();

                R result;

                try {
                    result = doProcess(data);
                } catch (RuntimeException e) {
                    SLF4jLogger.getLogger(Processor.class).error("{} could not process message `{}`", name(), data);
                    if (errorPipe().isConnected())
                    {
                        errorPipe().forward(data);
                        return;
                    } else {
                        throw new ProcessingException(this, e.getMessage(), e);
                    }
                }

                outputPipe().forward(result);

                finishedProcessing();

        } while (processAgain());
    }

    protected abstract R doProcess(T data);

    private boolean strictFailFastWarning()
    {
        return (filterContextRequired && (filterProperties().name().isEmpty())
                && !isPropertiesRequired());
    }

    public static  <T, R> Processor<T, R> processor(SerializableBiFunction<T, FilterContext, R> processFunction)
    {
        return new Processor<>(true, methodNameFromLambda(processFunction)) {
            @Override
            protected R doProcess(T data) {
                return processFunction.apply(data, filterContext());
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(PipedProcessor<T, R> pipedProcessor)
    {
        return new Processor<>(true, methodNameFromLambda(pipedProcessor)) {
            @Override
            protected R doProcess(T data) {
                pipedProcessor.processData(data, filterContext(), outputPipe());
                return null;
            }
        };
    }

    public static  <T, R> Processor<T, R> processor(SerializableFunction<T, R> processFunction)
    {
        return new Processor<>(false, methodNameFromLambda(processFunction)) {
            @Override
            protected R doProcess(T data) {
                return processFunction.apply(data);
            }
        };
    }

    public static  <T> Processor<T, T> consumer(SerializableBiConsumer<T, FilterContext> processFunction)
    {
        return new Processor<>(true, methodNameFromLambda(processFunction)) {
            @Override
            protected T doProcess(T data) {
                processFunction.accept(data, filterContext());
                return null;
            }
        };
    }


    public static  <T> Processor<T, T> consumer(SerializableConsumer<T> processFunction)
    {
        return new Processor<>(false, methodNameFromLambda(processFunction)) {
            @Override
            protected T doProcess(T data) {
                processFunction.accept(data);
                return null;
            }
        };
    }


}
