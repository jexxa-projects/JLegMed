package io.jexxa.jlegmed.core.filter;

import java.io.Serial;

public class ProcessingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1234567L;

    private final transient Filter causedFilter;
    public ProcessingException(Filter causedFilter, String message, Throwable cause)
    {
        super(message, cause);
        this.causedFilter = causedFilter;
    }

    public Filter causedFilter(){
        return causedFilter;
    }
}
