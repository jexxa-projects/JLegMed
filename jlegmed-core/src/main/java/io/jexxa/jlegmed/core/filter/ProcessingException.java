package io.jexxa.jlegmed.core.filter;

import java.io.Serial;

public class ProcessingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String causedFilter;
    public ProcessingException(String causedFilter, String message, Throwable cause)
    {
        super(message, cause);
        this.causedFilter = causedFilter;
    }

    public String causedFilter(){
        return causedFilter;
    }
}
