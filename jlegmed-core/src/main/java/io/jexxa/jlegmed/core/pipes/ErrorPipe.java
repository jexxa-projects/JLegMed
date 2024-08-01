package io.jexxa.jlegmed.core.pipes;

import io.jexxa.jlegmed.core.filter.ProcessingError;

import java.math.BigInteger;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static java.math.BigInteger.valueOf;

public class ErrorPipe<T> extends OutputPipe<ProcessingError<T>> {
    private BigInteger processingErrorCount = valueOf(0);
    private BigInteger handledProcessingErrors = valueOf(0);
    private BigInteger unhandledProcessingErrors = valueOf(0);

    public BigInteger processingErrorCount() { return processingErrorCount; }
    public BigInteger handledProcessingErrors() { return handledProcessingErrors; }
    public BigInteger unhandledProcessingErrors() { return unhandledProcessingErrors; }

    @Override
    public void forward(ProcessingError<T> data) {
        getInvocationHandler(this).invoke(this, this::forwardErrorToSuccessor, data);
    }

    private void forwardErrorToSuccessor(ProcessingError<T> processingError) {
        if (processingError == null) {
            return;
        }

        processingErrorCount = processingErrorCount.add(valueOf(1));

        if (!isConnected())
        {
            var filterName = processingError.processingException().causedFilter().name();

            getLogger(ErrorPipe.class).error("{}: Could not process message and all errorPipes are unconnected", filterName);
            getLogger(ErrorPipe.class).error("{}: Message: `{}` ", filterName, processingError.originalMessage());
            if (processingError.processingException().getCause() != null)
            {
                getLogger(ErrorPipe.class).error("{}: Reason: {}: {} ", filterName, processingError.processingException().getCause().getClass().getSimpleName(), processingError.processingException().getCause().getMessage());
            } else {
                getLogger(ErrorPipe.class).error("{}: Reason: {}: {} ", filterName, processingError.processingException().getClass().getSimpleName(), processingError.processingException().getMessage());
            }
            unhandledProcessingErrors = unhandledProcessingErrors.add(valueOf(1));
        } else {
            inputPipe().receive(processingError);
            handledProcessingErrors = handledProcessingErrors.add(valueOf(1));
        }
    }
}
