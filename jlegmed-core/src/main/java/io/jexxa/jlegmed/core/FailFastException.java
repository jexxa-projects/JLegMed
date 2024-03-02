package io.jexxa.jlegmed.core;

import java.io.Serial;

public class FailFastException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public FailFastException(String message)
    {
        super(message);
    }

    public FailFastException(String message, Throwable cause)
    {
        super(message,cause);
    }
}
