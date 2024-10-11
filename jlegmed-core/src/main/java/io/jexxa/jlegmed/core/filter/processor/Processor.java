package io.jexxa.jlegmed.core.filter.processor;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableBiConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.Filter;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.pipes.ErrorPipe;
import io.jexxa.jlegmed.core.pipes.InputPipe;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.methodNameFromLambda;

public abstract class Processor<T, R>  extends Filter {
    private final InputPipe<T> inputPipe = new InputPipe<>(this);
    private final OutputPipe<R> outputPipe = new OutputPipe<>();
    private final ErrorPipe<T> errorPipe = new ErrorPipe<>();
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
    public ErrorPipe<T> errorPipe()
    {
        return errorPipe;
    }

    public void process(T data) {

        do {
                startProcessing();

                try {
                    outputPipe().forward(doProcess(data));
                } catch (ProcessingException e) { // ProcessingException is either forwardedMessages to the errorPipe or rethrow
                    handleProcessingException(e, data);
                }   catch (RuntimeException e) {
                    handleRuntimeException(e, data);
                }

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

    private void handleRuntimeException(RuntimeException e, T data)
    {
        if (errorPipe().isConnected())
        {
            SLF4jLogger.getLogger(Processor.class).error("{} : Could not process message of input type {} -> forwarding it to error pipe", name(), data.getClass().getSimpleName());
            logErrorCause(e);
            errorPipe().forward(new ProcessingError<>(data, new ProcessingException(this, "Failed to process message", e)));

        } else {
            SLF4jLogger.getLogger(Processor.class).error("{}: Could not process message -> forwarding ProcessingException to successor", name());
            logErrorCause(e);

            throw new ProcessingException(this, e.getMessage(), e);
        }
    }

    private void logErrorCause(RuntimeException e)
    {
        if (e.getCause() != null)
        {
            SLF4jLogger.getLogger(Processor.class).error("{}: Reason: {}: {} ", name(), e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
        } else {
            SLF4jLogger.getLogger(Processor.class).error("{}: Reason: {}: {} ", name(), e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void handleProcessingException(RuntimeException e, T data)
    {
        if (errorPipe().isConnected()) {
            errorPipe().forward(new ProcessingError<>(data, new ProcessingException(this, "Failed to process message", e)));
        } else {
            throw e;
        }
    }

}
