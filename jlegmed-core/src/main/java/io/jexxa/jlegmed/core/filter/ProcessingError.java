package io.jexxa.jlegmed.core.filter;

public record ProcessingError<T>(T originalMessage, ProcessingException processingException) { }
