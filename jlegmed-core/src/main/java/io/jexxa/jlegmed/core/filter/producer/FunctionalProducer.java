package io.jexxa.jlegmed.core.filter.producer;

import io.jexxa.adapterapi.invocation.function.SerializableBiFunction;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;

import java.util.Objects;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.adapterapi.invocation.context.LambdaUtils.classNameFromLambda;

public abstract class FunctionalProducer<T> extends PassiveProducer<T> {

    private final String name;
    protected FunctionalProducer(String name, Class<?> classFromLambda) {
        super(classFromLambda);
        this.name = name;
    }

    protected FunctionalProducer(Class<T> sourceType, String name) {
        super(sourceType);
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void produceData() {
        do {
            startProcessing();

            T producedMessage = null;
            try {
                producedMessage = doProduce();
                forwardMessage(producedMessage);
            } catch (ProcessingException e)
            {
                errorPipe().forward(new ProcessingError<>(producedMessage, e));
            } catch (RuntimeException e)
            {
                errorPipe().forward(new ProcessingError<>(producedMessage, new ProcessingException(this.name(), name() + " could not produce message", e)));
            }

            finishedProcessing();

        } while (processAgain());
    }

    private void forwardMessage(T message)
    {
        getInvocationHandler(this)
                .invoke(this, () -> outputPipe().forward(message));

    }

    protected abstract T doProduce();

    public static <T> FunctionalProducer<T> producer(SerializableBiFunction<FilterContext, Class<T>, T> function, Class<T> producingType) {
        return new FunctionalProducer<>(producingType, filterNameFromLambda(function)) {
            @Override
            public void init() {
                Objects.requireNonNull(producingType());
            }

            @Override
            protected T doProduce() {
                return function.apply(filterContext(), producingType());
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(SerializableBiFunction<FilterContext, Class<T>, T> function) {
        return producer(function, null);
    }


    public static <T> FunctionalProducer<T> producer(SerializableFunction<FilterContext, T> function) {
        return new FunctionalProducer<>(
                filterNameFromLambda(function),
                classNameFromLambda(function))
        {
            @Override
            protected T doProduce() {
                return function.apply(filterContext());
            }
        };
    }


    public static <T> FunctionalProducer<T> producer(SerializableSupplier<T> function) {
        return new FunctionalProducer<>(
                filterNameFromLambda(function),
                classNameFromLambda(function))
        {
            @Override
            protected T doProduce() {
                return function.get();
            }
        };
    }

    public static <T> FunctionalProducer<T> producer(PipedProducer<T> pipedProducer) {
        return new FunctionalProducer<>(
                filterNameFromLambda(pipedProducer),
                classNameFromLambda(pipedProducer))
        {
            @Override
            protected T doProduce() {
                pipedProducer.produceData(filterContext(), outputPipe());
                return null;
            }
        };
    }

    public static <T> FunctionalProducer<T> consumer(SerializableConsumer<FilterContext> function) {
        return new FunctionalProducer<>(
                filterNameFromLambda(function),
                classNameFromLambda(function))
        {
            @Override
            protected T doProduce() {
                function.accept(filterContext());
                return null;
            }
        };
    }
}
