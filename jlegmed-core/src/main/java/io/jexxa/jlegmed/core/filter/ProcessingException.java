package io.jexxa.jlegmed.core.filter;

public class ProcessingException extends RuntimeException {
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
